package com.example.kmp_template.server.person.domain.model

import kotlin.uuid.Uuid

data class Person(
    val id: Uuid,
    val name: String,
)