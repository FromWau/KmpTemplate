package com.example.kmp_template.shared_client.settings.presentation

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SettingsAction {
    data object OnBackClicked : SettingsAction

    sealed interface LoadedSetting : SettingsAction {
        data class OnValueChanged(val form: SettingsState.SettingForm) : LoadedSetting
        data class OnDeleteSetting(val form: SettingsState.SettingForm) : LoadedSetting
    }

    sealed interface NewSetting : SettingsAction {
        data class OnKeyChanged(val key: String) : NewSetting
        data class OnValueChanged(val value: String) : NewSetting
    }

    data object OnSaveSettings : SettingsAction
}
