package com.example.kmp_template.shared_client.core.toast

import androidx.compose.runtime.Immutable
import com.example.kmp_template.shared_client.core.StringValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

@Immutable
data class ToastMessage(
    val id: Uuid,
    val type: ToastType,
    val message: StringValue,
    val duration: Duration = 3.seconds,
    val isDismissable: Boolean = true,
    val actions: ImmutableList<ToastAction> = persistentListOf(),
)

enum class ToastType {
    INFO,
    WARNING,
    ERROR,
    SUCCESS,
}

@Immutable
data class ToastAction(
    val label: StringValue,
    val onClick: () -> Unit,
)