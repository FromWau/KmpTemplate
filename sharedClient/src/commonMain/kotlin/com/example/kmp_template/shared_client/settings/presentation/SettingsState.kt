package com.example.kmp_template.shared_client.settings.presentation

import com.example.kmp_template.shared_client.core.presentation.StringValue

data class SettingsState(
    // When null, it means loading
    val settingsForm: List<SettingForm>? = null,

    val newSettingForm: NewForm = NewForm(),

    val isFormValid: Boolean = true,
) {
    data class SettingForm(
        val key: String,
        val value: String,
        val valueErrors: List<StringValue> = emptyList(),
    )

    data class NewForm(
        val key: String? = null,
        val keyErrors: List<StringValue> = emptyList(),
        val value: String? = null,
        val valueErrors: List<StringValue> = emptyList(),
    )
}
