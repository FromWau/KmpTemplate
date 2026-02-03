package com.example.kmp_template.core.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlOutputConfig
import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.config.model.Config
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.core.result.EmptyResult
import com.example.kmp_template.core.result.Error
import com.example.kmp_template.core.result.Result
import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer

class ConfigManager(
    systemDirs: SystemAppDirectories,
) {
    companion object {
        private const val TAG = "ConfigManager"
    }

    private val configDir = systemDirs.configDir()

    // Custom TOML encoder with no indentation for clean output
    private val tomlEncoder = Toml(
        outputConfig = TomlOutputConfig(
            indentation = TomlIndentation.NONE
        )
    )


    @OptIn(InternalSerializationApi::class)
    fun <T : Config> load(config: T): Result<T, ConfigError.LoadError> {
        Log.tag(TAG).d { "Loading config: ${config.name}" }
        if (config.name.isBlank()) {
            return Result.Error(ConfigError.LoadError.FileNameBlank)
        }

        if (!SystemFileSystem.exists(configDir)) {
            Log.tag(TAG).w { "Config directory does not exist: $configDir" }
            return Result.Error(ConfigError.LoadError.FileNotFound(configDir))
        }

        val configFile = config.filePath
        try {
            val content = SystemFileSystem.source(configFile).buffered().use {
                it.readString()
            }

            @Suppress("UNCHECKED_CAST")
            val serializer = config::class.serializer() as kotlinx.serialization.KSerializer<T>
            val loadedConfig = Toml.decodeFromString(serializer, content)
            Log.tag(TAG).i { "Loaded config from: $configFile" }
            return Result.Success(loadedConfig)

        } catch (e: SerializationException) {
            Log.tag(TAG).e { "Failed to parse config at $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.LoadError.ParseError(e.message ?: "Unknown serialization error")
            )

        } catch (e: IOException) {
            Log.tag(TAG).e { "Failed to read config at $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.LoadError.FileNotFound(configFile)
            )

        } catch (e: Exception) {
            Log.tag(TAG).e { "Failed to load config at $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.LoadError.GenericError(e)
            )
        }
    }

    @OptIn(InternalSerializationApi::class)
    fun save(config: Config): EmptyResult<ConfigError.SaveError> {
        try {
            SystemFileSystem.createDirectories(configDir, mustCreate = false)
        } catch (e: IOException) {
            Log.tag(TAG).e { "Failed to create config directory at $configDir: ${e.message}" }
            return Result.Error(
                ConfigError.SaveError.CanNotCreateConfigDir(configDir)
            )
        }

        val configFile = config.filePath
        try {
            @Suppress("UNCHECKED_CAST")
            val serializer = config::class.serializer() as kotlinx.serialization.KSerializer<Config>
            val toml = tomlEncoder.encodeToString(serializer, config)
            SystemFileSystem.sink(configFile).buffered().use {
                it.writeString(toml)
            }
            Log.tag(TAG).i { "Saved config to: $configFile" }
            return Result.Success(Unit)

        } catch (e: IOException) {
            Log.tag(TAG).e { "Failed to save config to $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.SaveError.CanNotWriteToFile(configFile)
            )

        } catch (e: SerializationException) {
            Log.tag(TAG)
                .e { "Failed to serialize config for saving to $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.SaveError.ParseError(e.message ?: "Unknown serialization error")
            )

        } catch (e: Exception) {
            Log.tag(TAG).e { "Unexpected error saving config to $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.SaveError.GenericError(e)
            )
        }
    }

    fun saveDefaults(config: Config): EmptyResult<ConfigError.SaveError> {
        try {
            SystemFileSystem.createDirectories(configDir, mustCreate = false)
        } catch (e: IOException) {
            Log.tag(TAG).e { "Failed to create config directory at $configDir: ${e.message}" }
            return Result.Error(
                ConfigError.SaveError.CanNotCreateConfigDir(configDir)
            )
        }

        val configFile = config.filePath
        try {
            val toml = config.defaultContent
            SystemFileSystem.sink(configFile).buffered().use {
                it.writeString(toml)
            }
            Log.tag(TAG).i { "Saved default config to: $configFile" }
            return Result.Success(Unit)

        } catch (e: IOException) {
            Log.tag(TAG).e { "Failed to save default config to $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.SaveError.CanNotWriteToFile(configFile)
            )

        } catch (e: Exception) {
            Log.tag(TAG).e { "Unexpected error saving default config to $configFile: ${e.message}" }
            return Result.Error(
                ConfigError.SaveError.GenericError(e)
            )
        }
    }

    private val Config.filePath: Path
        get() {
            return Path(configDir, "${this.name}.toml")
        }
}


sealed interface ConfigError {
    sealed interface LoadError : ConfigError, Error {
        data object FileNameBlank : LoadError
        data class FileNotFound(val path: Path) : LoadError
        data class ParseError(val message: String) : LoadError
        data class GenericError(val throwable: Throwable) : LoadError
    }

    sealed interface SaveError : ConfigError, Error {
        data class CanNotCreateConfigDir(val path: Path) : SaveError
        data class CanNotWriteToFile(val path: Path) : SaveError
        data class ParseError(val message: String) : SaveError
        data class GenericError(val throwable: Throwable) : SaveError
    }
}