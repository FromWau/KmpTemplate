package com.example.kmp_template.shared_client.person.presentation

import androidx.compose.runtime.Immutable
import com.example.kmp_template.shared_client.core.FormField
import kotlinx.collections.immutable.ImmutableList
import kotlin.uuid.Uuid

@Immutable
data class PersonState(
    // When null, it means loading
    val remotePersonForm: ImmutableList<PersonForm>? = null,
    val localPersonForm: ImmutableList<PersonForm>? = null,

    val newPersonForm: NewPersonForm = NewPersonForm(),

    val isFormValid: Boolean = true,
) {
    @Immutable
    data class PersonForm(
        val id: Uuid,
        val nameField: FormField<String>,
        val local: Boolean,
    )

    @Immutable
    data class NewPersonForm(
        val nameField: FormField<String?> = FormField(null),
        val localField: FormField<Boolean?> = FormField(null),
    )
}
