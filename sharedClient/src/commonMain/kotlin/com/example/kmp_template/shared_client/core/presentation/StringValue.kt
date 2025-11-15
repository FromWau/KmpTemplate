package com.example.kmp_template.shared_client.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface StringValue {
    data class HardCoded(val value: String) : StringValue
    data class Id(val id: StringResource, val args: List<Any> = emptyList()) : StringValue

    @Composable
    fun asString(): String {
        return when (this) {
            is HardCoded -> value
            is Id -> stringResource(id, *args.toTypedArray())
        }
    }
}