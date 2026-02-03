package com.example.kmp_template.shared_client.setting.domain.repository

import com.example.kmp_template.shared_client.setting.domain.model.Setting
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    suspend fun getSetting(): Setting

    fun getSettingFlow(): Flow<Setting>

    suspend fun upsertSetting(setting: Setting): Boolean

    suspend fun deleteSetting(setting: Setting.Setting): Boolean
}
