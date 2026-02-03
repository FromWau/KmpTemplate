@file:Suppress("unused")

package com.example.kmp_template.shared_client.core.toast

import com.example.kmp_template.shared_client.core.StringValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class ToastService {
    private val _toastEvents = MutableSharedFlow<ToastMessage>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val toastEvents: SharedFlow<ToastMessage> = _toastEvents.asSharedFlow()

    fun showInfo(
        message: StringValue,
        isDismissable: Boolean = true,
        duration: Duration = 1.seconds,
        actions: ImmutableList<ToastAction> = persistentListOf(),
    ) {
        _toastEvents.tryEmit(
            ToastMessage(
                id = Uuid.random(),
                type = ToastType.INFO,
                message = message,
                duration = duration,
                isDismissable = isDismissable,
                actions = actions,
            )
        )
    }

    fun showWarning(
        message: StringValue,
        isDismissable: Boolean = true,
        duration: Duration = 2.seconds,
        actions: ImmutableList<ToastAction> = persistentListOf(),
    ) {
        _toastEvents.tryEmit(
            ToastMessage(
                id = Uuid.random(),
                type = ToastType.WARNING,
                message = message,
                duration = duration,
                isDismissable = isDismissable,
                actions = actions,
            )
        )
    }

    fun showError(
        message: StringValue,
        isDismissable: Boolean = true,
        duration: Duration = 3.seconds,
        actions: ImmutableList<ToastAction> = persistentListOf(),
    ) {
        _toastEvents.tryEmit(
            ToastMessage(
                id = Uuid.random(),
                type = ToastType.ERROR,
                message = message,
                duration = duration,
                isDismissable = isDismissable,
                actions = actions,
            )
        )
    }

    fun showSuccess(
        message: StringValue,
        isDismissable: Boolean = true,
        duration: Duration = 1.seconds,
        actions: ImmutableList<ToastAction> = persistentListOf(),
    ) {
        _toastEvents.tryEmit(
            ToastMessage(
                id = Uuid.random(),
                type = ToastType.SUCCESS,
                message = message,
                duration = duration,
                isDismissable = isDismissable,
                actions = actions,
            )
        )
    }
}
