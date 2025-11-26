package com.example.kmp_template.shared_client.settings.presentation.mapper

import com.example.kmp_template.shared_client.settings.domain.model.Settings
import com.example.kmp_template.shared_client.settings.presentation.SettingsState

fun Settings.toSettingsForm(): List<SettingsState.SettingForm> {
    return this.settings.map { SettingsState.SettingForm(key = it.key, value = it.value) }
}

fun SettingsState.SettingForm.toSetting(): Settings.Setting {
    return Settings.Setting(key = this.key, value = this.value)
}

fun SettingsState.NewForm.toSetting(): Settings.Setting? {
    if (this.key == null || this.value == null) {
        return null
    }

    return Settings.Setting(key = this.key, value = this.value)
}
