package com.example.kmp_template.shared_client.feature.domain.model

import kotlin.uuid.Uuid

data class Model(
    val id: Uuid,
    val name: String,
)