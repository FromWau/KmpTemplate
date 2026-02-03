package com.example.kmp_template.shared_client.setting.data

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_client.core.StringValue
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.setting.data.database.SettingDao
import com.example.kmp_template.shared_client.setting.data.mapper.toSetting
import com.example.kmp_template.shared_client.setting.data.mapper.toSettingEntities
import com.example.kmp_template.shared_client.setting.data.mapper.toSettingEntity
import com.example.kmp_template.shared_client.setting.domain.model.Setting
import com.example.kmp_template.shared_client.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SettingRepositoryImpl(
    private val settingDao: SettingDao,
    private val toastService: ToastService,
) : SettingRepository {
    companion object {
        const val TAG = "SettingRepository"
    }

    override suspend fun getSetting(): Setting {
        try {
            return settingDao.getAll().toSetting()
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to get settings" }
            toastService.showError(StringValue.Raw("Failed to load settings"))
            return Setting(emptyList())
        }
    }

    override fun getSettingFlow(): Flow<Setting> {
        try {
            return settingDao.getAllFlow().map { entities ->
                entities.toSetting()
            }
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to get settings flow" }
            toastService.showError(StringValue.Raw("Failed to load settings"))
            return flowOf(Setting(emptyList()))
        }
    }

    override suspend fun upsertSetting(setting: Setting): Boolean {
        try {
            settingDao.upsert(*setting.toSettingEntities().toTypedArray())
            return true
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to upsert setting" }
            toastService.showError(StringValue.Raw("Failed to save setting"))
            return false
        }
    }

    override suspend fun deleteSetting(setting: Setting.Setting): Boolean {
        try {
            return settingDao.delete(setting.toSettingEntity()) == 1
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to delete setting" }
            toastService.showError(StringValue.Raw("Failed to delete setting"))
            return false
        }
    }
}
