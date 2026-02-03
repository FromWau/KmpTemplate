package com.example.kmp_template.shared_client.setting.data.mapper

import com.example.kmp_template.shared_client.setting.data.database.SettingEntity
import com.example.kmp_template.shared_client.setting.domain.model.Setting

fun List<SettingEntity>.toSetting(): Setting {
    val setting = this.map {
        Setting.Setting(key = it.key, value = it.value)
    }

    return Setting(setting)
}


fun Setting.Setting.toSettingEntity(): SettingEntity {
    return SettingEntity(key = key, value = value)
}

fun Setting.toSettingEntities(): List<SettingEntity> {
    return settings.map { it.toSettingEntity() }
}
