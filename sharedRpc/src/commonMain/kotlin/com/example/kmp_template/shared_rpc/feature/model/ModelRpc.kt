package com.example.kmp_template.shared_rpc.feature.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ModelRpc(
    val id: Uuid,
    val name: String,
)
