package com.example.kmp_template.shared_client.config

import com.example.kmp_template.shared_client.setting.domain.model.Setting

fun SettingConfig.toDomain(): Setting {

    return Setting(
        settings.map {
            Setting.Setting(
                key = it.key,
                value = it.value
            )
        }
    )
}

fun Setting.toSettingConfig(): SettingConfig {
    return SettingConfig(
        settings.associate {
            it.key to it.value
        }
    )
}
