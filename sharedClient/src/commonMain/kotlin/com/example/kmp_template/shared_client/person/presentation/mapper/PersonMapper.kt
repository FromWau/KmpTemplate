package com.example.kmp_template.shared_client.person.presentation.mapper

import com.example.kmp_template.shared_client.core.FormField
import com.example.kmp_template.shared_client.person.domain.model.Person
import com.example.kmp_template.shared_client.person.presentation.PersonState
import kotlin.uuid.Uuid

fun Person.toPersonForm(): PersonState.PersonForm =
    PersonState.PersonForm(
        id = this.id,
        nameField = FormField(this.name),
        local = this.local,
    )

fun PersonState.PersonForm.toDomain(): Person =
    Person(id = this.id, name = this.nameField.value, local = this.local)

fun PersonState.NewPersonForm.toDomain(): Person? {
    if (this.nameField.value == null || this.localField.value == null) {
        return null
    }

    return Person(
        id = Uuid.random(),
        name = this.nameField.value,
        local = this.localField.value,
    )
}
