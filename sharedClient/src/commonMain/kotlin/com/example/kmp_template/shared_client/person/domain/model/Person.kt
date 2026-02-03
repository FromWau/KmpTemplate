package com.example.kmp_template.shared_client.person.domain.model

import kotlin.uuid.Uuid

data class Person(
    val id: Uuid,
    val name: String,
    val local: Boolean,
)