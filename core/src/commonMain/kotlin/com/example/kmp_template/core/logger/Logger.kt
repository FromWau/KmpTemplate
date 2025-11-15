@file:Suppress("unused")
@file:OptIn(ExperimentalTime::class)

package com.example.kmp_template.core.logger

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

expect val Log: Logger

interface Logger {
    fun log(tag: String, logLevel: LogLevel, lazyMessage: () -> String)
    fun log(tag: String, logLevel: LogLevel, throwable: Throwable)
    fun tag(tag: String): TaggedLogger = TaggedLogger(tag, this)
}

fun toLogString(tag: String, logLevel: LogLevel, lazyMessage: () -> String): String {
    val timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = formatDateTime(timestamp)
    val level = logLevel.name.padEnd(5) // pad to 5 characters

    val logMsg = "$formattedDate $level ${tag.take(35)} - ${lazyMessage()}"
    return logMsg
}

fun toLogString(tag: String, logLevel: LogLevel, throwable: Throwable): String {
    val timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = formatDateTime(timestamp)
    val level = logLevel.name.padEnd(5) // pad to 5 characters

    val logMsg = "$formattedDate $level ${tag.take(35)} - ${throwable.stackTraceToString()}"
    return logMsg
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