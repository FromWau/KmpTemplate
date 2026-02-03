@file:Suppress("unused")
@file:OptIn(ExperimentalTime::class)

package com.example.kmp_template.core.logger

import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.config.model.LogFormat
import com.example.kmp_template.core.config.model.LoggingConfig
import com.example.kmp_template.core.logDir
import com.example.kmp_template.core.resolveTilde
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import com.example.kmp_template.core.config.model.LogLevel as ConfigLogLevel

expect val Log: Logger

sealed interface LoggerState {
    data object Initializing : LoggerState
    data class Ready(
        val config: LoggingConfig,
        val systemAppDirectories: SystemAppDirectories,
    ) : LoggerState {
        val file: Path = run {
            val logDir =
                config.logDir
                    ?.let { Path(it) }
                    ?: systemAppDirectories.logDir()

            // Expand tilde to home directory
            val expandedDir =
                Path(logDir).resolveTilde(systemAppDirectories)

            val timestampLocal =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val formattedDate = formatDateTime(timestampLocal)
            val filename = "$formattedDate.log"

            return@run Path(expandedDir, filename)
        }
    }
}

@Serializable
data class LogEntry(
    val timestamp: Instant,
    val tag: String,
    val logLevel: LogLevel,
    val message: String,
)

internal fun LogEntry.toJsonString(): String {
    return Json.encodeToString(LogEntry.serializer(), this)
}

internal fun LogEntry.toTextString(): String {
    val timestampLocal = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = formatDateTime(timestampLocal)
    val level = logLevel.name.padEnd(7)
    return "$formattedDate $level ${tag.take(35)} - $message"
}

enum class LogLevel {
    VERBOSE, DEBUG, INFO, WARN, ERROR
}

class TaggedLogger(
    private val tag: String,
    private val delegate: Logger,
) {
    fun v(msg: () -> String) = delegate.log(tag, LogLevel.VERBOSE, msg)
    fun d(msg: () -> String) = delegate.log(tag, LogLevel.DEBUG, msg)
    fun i(msg: () -> String) = delegate.log(tag, LogLevel.INFO, msg)
    fun w(msg: () -> String) = delegate.log(tag, LogLevel.WARN, msg)
    fun e(msg: () -> String) = delegate.log(tag, LogLevel.ERROR, msg)
    fun e(throwable: Throwable) = delegate.log(tag, LogLevel.ERROR, throwable)
    fun e(throwable: Throwable, msg: () -> String) {
        delegate.log(tag, LogLevel.ERROR, throwable)
        delegate.log(tag, LogLevel.ERROR, msg)
    }
}

abstract class Logger {
    companion object {
        private const val TAG = "Logger"
    }

    private val lock = reentrantLock()
    private var state: LoggerState = LoggerState.Initializing
    private val bufferedLogs = mutableListOf<LogEntry>()
    private var fileSink: Sink? = null

    /**
     * Initialize the logger with config and flush buffered logs.
     * Should be called once after config is loaded.
     */
    fun initialize(config: LoggingConfig, systemDirs: SystemAppDirectories) {
        Log.tag(TAG).d { "Initializing logger with config: $config" }

        lock.withLock {
            if (state is LoggerState.Ready) {
                Log.tag(TAG).w { "Logger already initialized, ignoring duplicate call" }
                return@withLock
            }

            // Set state to Ready BEFORE flushing
            state = LoggerState.Ready(config, systemDirs)

            // Flush all buffered logs
            Log.tag(TAG).d { "Flushing buffered logs" }
            bufferedLogs.forEach { buffered ->
                log(buffered.tag, buffered.logLevel) { buffered.message }
            }
            Log.tag(TAG).d { "Flushed ${bufferedLogs.size} buffered logs" }
            bufferedLogs.clear()
        }
    }

    fun log(tag: String, logLevel: LogLevel, lazyMessage: () -> String) {
        when (val currentState = lock.withLock { state }) {
            is LoggerState.Initializing -> {
                val entry = LogEntry(
                    timestamp = Clock.System.now(),
                    tag = tag,
                    logLevel = logLevel,
                    message = lazyMessage()
                )
                lock.withLock {
                    // Re-check: state may have transitioned during entry creation
                    when (val rechecked = state) {
                        is LoggerState.Initializing -> bufferedLogs.add(entry)
                        is LoggerState.Ready -> dispatchLog(entry, logLevel, rechecked)
                    }
                }
            }

            is LoggerState.Ready -> {
                // Only evaluate the lazy message if the level passes
                if (shouldLog(logLevel, currentState.config)) {
                    val entry = LogEntry(
                        timestamp = Clock.System.now(),
                        tag = tag,
                        logLevel = logLevel,
                        message = lazyMessage()
                    )
                    dispatchLog(entry, logLevel, currentState)
                }
            }
        }
    }

    private fun dispatchLog(entry: LogEntry, logLevel: LogLevel, readyState: LoggerState.Ready) {
        if (!shouldLog(logLevel, readyState.config)) return
        if (readyState.config.consoleEnabled) {
            doLog(entry, readyState.config)
        }
        if (readyState.config.logToFile) {
            writeToFile(logEntry = entry, config = readyState.config, file = readyState.file)
        }
    }

    fun log(tag: String, logLevel: LogLevel, throwable: Throwable) {
        log(tag, logLevel) { throwable.stackTraceToString() }
    }

    fun tag(tag: String): TaggedLogger = TaggedLogger(tag, this)

    protected abstract fun doLog(logEntry: LogEntry, config: LoggingConfig)

    private fun getOrOpenFileSink(file: Path): Sink {
        fileSink?.let { return it }

        file.parent?.let { parent ->
            SystemFileSystem.createDirectories(parent, mustCreate = false)
        }

        val sink = SystemFileSystem.sink(file, append = true).buffered()
        fileSink = sink
        return sink
    }

    private fun writeToFile(
        logEntry: LogEntry,
        config: LoggingConfig,
        file: Path,
    ) {
        val logString = when (config.format) {
            LogFormat.JSON -> logEntry.toJsonString()
            LogFormat.TEXT -> logEntry.toTextString()
        }

        try {
            lock.withLock {
                val sink = getOrOpenFileSink(file)
                sink.writeString("$logString\n")
                sink.flush()
            }
        } catch (e: Exception) {
            lock.withLock {
                try { fileSink?.close() } catch (_: Exception) {}
                fileSink = null
            }
            doLog(
                logEntry = LogEntry(
                    timestamp = Clock.System.now(),
                    tag = TAG,
                    logLevel = LogLevel.ERROR,
                    message = "Failed to write log to file: ${e.message}"
                ),
                config = config,
            )
        }
    }
}

private fun shouldLog(logLevel: LogLevel, config: LoggingConfig): Boolean {
    val configLevel = when (config.level) {
        ConfigLogLevel.VERBOSE -> LogLevel.VERBOSE
        ConfigLogLevel.DEBUG -> LogLevel.DEBUG
        ConfigLogLevel.INFO -> LogLevel.INFO
        ConfigLogLevel.WARN -> LogLevel.WARN
        ConfigLogLevel.ERROR -> LogLevel.ERROR
    }
    return logLevel >= configLevel
}

private fun formatDateTime(dt: LocalDateTime): String {
    val year = dt.year.toString().padStart(4, '0')
    val month = dt.month.number.toString().padStart(2, '0')
    val day = dt.day.toString().padStart(2, '0')
    val hour = dt.hour.toString().padStart(2, '0')
    val minute = dt.minute.toString().padStart(2, '0')
    val second = dt.second.toString().padStart(2, '0')
    val millisecond = (dt.nanosecond / 1_000_000).toString().padStart(3, '0')

    return "$year-$month-$day $hour:$minute:$second.$millisecond"
}
