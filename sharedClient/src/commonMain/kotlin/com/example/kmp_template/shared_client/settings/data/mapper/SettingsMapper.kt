package com.example.kmp_template.shared_client.settings.data.mapper

import com.example.kmp_template.shared_client.settings.data.database.SettingsEntity
import com.example.kmp_template.shared_client.settings.domain.model.Settings

fun List<SettingsEntity>.toSettings(): Settings {
    val settings = this.map {
        Settings.Setting(key = it.key, value = it.value)
    }

    return Settings(settings)
}


fun Settings.Setting.toSettingsEntity(): SettingsEntity {
    return SettingsEntity(key = key, value = value)
}

fun Settings.toSettingsEntities(): List<SettingsEntity> {
    return settings.map { it.toSettingsEntity() }
}
