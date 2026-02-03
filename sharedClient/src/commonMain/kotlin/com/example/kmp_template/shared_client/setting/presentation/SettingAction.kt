package com.example.kmp_template.shared_client.setting.presentation

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SettingAction {
    data object OnBackClicked : SettingAction

    sealed interface LoadedSetting : SettingAction {
        data class OnValueChanged(val form: SettingState.SettingForm) : LoadedSetting
        data class OnDeleteSetting(val form: SettingState.SettingForm) : LoadedSetting
    }

    sealed interface NewSetting : SettingAction {
        data class OnKeyChanged(val key: String) : NewSetting
        data class OnValueChanged(val value: String) : NewSetting
    }

    data object OnSaveSetting : SettingAction
}
