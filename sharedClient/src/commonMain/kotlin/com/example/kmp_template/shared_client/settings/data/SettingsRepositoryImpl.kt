package com.example.kmp_template.shared_client.settings.data

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_client.core.presentation.toast.ToastService
import com.example.kmp_template.shared_client.settings.data.database.SettingsDao
import com.example.kmp_template.shared_client.settings.data.mapper.toSettings
import com.example.kmp_template.shared_client.settings.data.mapper.toSettingsEntities
import com.example.kmp_template.shared_client.settings.data.mapper.toSettingsEntity
import com.example.kmp_template.shared_client.settings.domain.model.Settings
import com.example.kmp_template.shared_client.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val settingsDao: SettingsDao,
    private val toastService: ToastService,
) : SettingsRepository {
    companion object {
        const val TAG = "SettingsRepository"
    }

    override suspend fun getSettings(): Settings {
        try {
            return settingsDao.getAll().toSettings()
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to get settings" }
            toastService.showError("Failed to load settings")
            return Settings(emptyList())
        }
    }

    override fun getSettingsFlow(): Flow<Settings> {
        try {
            return settingsDao.getAllFlow().map { entities ->
                entities.toSettings()
            }
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to get settings flow" }
            toastService.showError("Failed to load settings")
            return flowOf(Settings(emptyList()))
        }
    }

    override suspend fun upsertSettings(settings: Settings): Boolean {
        try {
            settingsDao.upsert(*settings.toSettingsEntities().toTypedArray())
            return true
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to upsert settings" }
            toastService.showError("Failed to save settings")
            return false
        }
    }

    override suspend fun deleteSetting(setting: Settings.Setting): Boolean {
        try {
            return settingsDao.delete(setting.toSettingsEntity()) == 1
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to delete setting" }
            toastService.showError("Failed to delete setting")
            return false
        }
    }
}
