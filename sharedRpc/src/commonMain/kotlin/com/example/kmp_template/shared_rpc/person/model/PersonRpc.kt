package com.example.kmp_template.shared_rpc.person.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PersonRpc(
    val id: Uuid,
    val name: String,
)
