package com.example.kmp_template.shared_client.settings.presentation.mapper

import com.example.kmp_template.shared_client.core.presentation.FormField
import com.example.kmp_template.shared_client.settings.domain.model.Settings
import com.example.kmp_template.shared_client.settings.presentation.SettingsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun Settings.toSettingsForm(): ImmutableList<SettingsState.SettingForm> {
    return this.settings.map {
        SettingsState.SettingForm(
            key = it.key,
            valueField = FormField(it.value),
        )
    }.toImmutableList()
}

fun SettingsState.SettingForm.toSetting(): Settings.Setting {
    return Settings.Setting(key = this.key, value = this.valueField.value)
}

fun SettingsState.NewForm.toSetting(): Settings.Setting? {
    if (this.keyField.value == null || this.valueField.value == null) {
        return null
    }

    return Settings.Setting(key = this.keyField.value, value = this.valueField.value)
}
