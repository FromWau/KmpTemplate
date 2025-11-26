package com.example.kmp_template.shared_client.settings.domain.repository

import com.example.kmp_template.shared_client.settings.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Settings

    fun getSettingsFlow(): Flow<Settings>

    suspend fun upsertSettings(settings: Settings): Boolean

    suspend fun deleteSetting(setting: Settings.Setting): Boolean
}
