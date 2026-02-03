package com.example.kmp_template.shared_client.app

import androidx.lifecycle.ViewModel
import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.config.ConfigLoader
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.core.result.Result
import com.example.kmp_template.shared_client.config.AppConfig
import com.example.kmp_template.shared_client.config.toDomain
import com.example.kmp_template.shared_client.core.StringValue
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

class AppStartupViewModel(
    private val configLoader: ConfigLoader,
    private val toastService: ToastService,
    private val systemDirs: SystemAppDirectories,
    private val settingRepository: SettingRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "AppStartupViewModel"
    }

    private val _state = MutableStateFlow<AppStartupState>(AppStartupState.Initializing)
    val state = _state.asStateFlow()


    fun initializeApp() {
        toastService.showInfo(StringValue.Raw("Initializing application..."))

        // Load configuration synchronously during startup
        val config = when (val result = configLoader.load(AppConfig())) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.tag(TAG)
                    .w { "No config file found, creating default at: ${systemDirs.configDir()}/app.toml" }
                val default = AppConfig()

                configLoader.saveDefaults(default)
                when (val reloaded = configLoader.load(default)) {
                    is Result.Success -> reloaded.data
                    is Result.Error -> {
                        Log.tag(TAG).e { "Failed to create config file, using in-memory defaults" }
                        default
                    }
                }
            }
        }

        // Initialize logger with loaded config
        Log.initialize(config.logging, systemDirs)
        Log.tag(TAG).d { "Configuration loaded: $config" }

        // Apply config settings to database (config is source of truth on startup)
        runBlocking {
            val setting = config.settingConfig.toDomain()
            settingRepository.upsertSetting(setting)
            Log.tag(TAG).d { "Applied config settings to database: $setting" }
        }

        // Do here any other startup initialization tasks if needed

        _state.update { AppStartupState.Ready }
        toastService.showSuccess(StringValue.Raw("Application is ready."))
    }
}