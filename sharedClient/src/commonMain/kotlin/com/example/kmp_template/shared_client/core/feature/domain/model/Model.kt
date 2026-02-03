package com.example.kmp_template.shared_client.core.feature.domain.model

import kotlin.uuid.Uuid

data class Model(
    val id: Uuid,
    val name: String,
)