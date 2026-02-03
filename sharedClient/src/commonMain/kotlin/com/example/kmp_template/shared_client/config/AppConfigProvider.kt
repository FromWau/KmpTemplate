package com.example.kmp_template.shared_client.config

import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.config.ConfigError
import com.example.kmp_template.core.config.ConfigManager
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.core.result.Result
import com.example.kmp_template.core.result.onError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppConfigProvider(
    initConfig: AppConfig,
    private val configManager: ConfigManager,
) {
    companion object {
        private const val TAG = "AppConfigProvider"
    }

    private val _state: MutableStateFlow<AppConfig> = MutableStateFlow(initConfig)
    val config: StateFlow<AppConfig> = _state.asStateFlow()

    fun updateConfig(function: (AppConfig) -> AppConfig) {
        _state.update {
            val new = function(it)
            Log.tag(TAG).i { "AppConfig updated: $new" }

            configManager.save(new)
                .onError { error -> Log.tag(TAG).e { "Failed to persist config: $error" } }

            if (it.logging != new.logging) {
                Log.reconfigure(new.logging)
            }

            new
        }
    }
}


object AppConfigProviderFactory {
    private const val TAG = "AppConfigProviderFactory"

    fun create(
        configManager: ConfigManager,
        systemDirs: SystemAppDirectories,
    ): AppConfigProvider {
        // Load configuration synchronously during startup
        val initConfig = when (val result: Result<AppConfig, ConfigError.LoadError> =
            configManager.load(AppConfig())) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.tag(TAG)
                    .w { "No config file found, creating default at: ${systemDirs.configDir()}/app.toml" }
                val default = AppConfig()

                configManager.save(default)
                when (val reloaded = configManager.load(default)) {
                    is Result.Success -> reloaded.data
                    is Result.Error -> {
                        Log.tag(TAG).e { "Failed to create config file, using in-memory defaults" }
                        default
                    }
                }
            }
        }

        return AppConfigProvider(initConfig, configManager)
    }
}