package com.example.kmp_template.shared_client.settings.presentation

import androidx.compose.runtime.Immutable
import com.example.kmp_template.shared_client.core.presentation.FormField
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class SettingsState(
    // When null, it means loading
    val settingsForm: ImmutableList<SettingForm>? = null,

    val newSettingForm: NewForm = NewForm(),

    val isFormValid: Boolean = true,
) {
    @Immutable
    data class SettingForm(
        val key: String,
        val valueField: FormField<String>,
    )

    @Immutable
    data class NewForm(
        val keyField: FormField<String?> = FormField(null),
        val valueField: FormField<String?> = FormField(null),
    )
}
