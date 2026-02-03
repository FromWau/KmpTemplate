package com.example.kmp_template.shared_client.setting.presentation.mapper

import com.example.kmp_template.shared_client.core.FormField
import com.example.kmp_template.shared_client.setting.domain.model.Setting
import com.example.kmp_template.shared_client.setting.presentation.SettingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun Setting.toSettingForm(): ImmutableList<SettingState.SettingForm> {
    return this.settings.map {
        SettingState.SettingForm(
            key = it.key,
            valueField = FormField(it.value),
        )
    }.toImmutableList()
}

fun SettingState.SettingForm.toSetting(): Setting.Setting {
    return Setting.Setting(key = this.key, value = this.valueField.value)
}

fun SettingState.NewForm.toSetting(): Setting.Setting? {
    if (this.keyField.value == null || this.valueField.value == null) {
        return null
    }

    return Setting.Setting(key = this.keyField.value, value = this.valueField.value)
}
