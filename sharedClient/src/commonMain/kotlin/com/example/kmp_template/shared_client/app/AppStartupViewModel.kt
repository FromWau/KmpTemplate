package com.example.kmp_template.shared_client.app

import androidx.lifecycle.ViewModel
import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_client.config.AppConfigProvider
import com.example.kmp_template.shared_client.config.toDomain
import com.example.kmp_template.shared_client.core.StringValue
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

class AppStartupViewModel(
    private val appConfigProvider: AppConfigProvider,
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
        val config = appConfigProvider.config.value

        // Initialize logger with loaded config
        Log.initialize(config.logging, systemDirs)
        Log.tag(TAG).d { "Configuration loaded" }

        // Apply config settings to database
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