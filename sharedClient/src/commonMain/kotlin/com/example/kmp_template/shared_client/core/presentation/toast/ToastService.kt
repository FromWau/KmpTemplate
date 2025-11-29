@file:Suppress("unused")

package com.example.kmp_template.shared_client.core.presentation.toast

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ToastService {
    private val _toastEvents = MutableSharedFlow<ToastMessage>(
        extraBufferCapacity = 1, // Buffer one toast if processing another
    )
    val toastEvents: SharedFlow<ToastMessage> = _toastEvents.asSharedFlow()

    fun showSuccess(message: String) {
        _toastEvents.tryEmit(
            ToastMessage(
                message = message,
                type = ToastType.SUCCESS,
            )
        )
    }

    fun showError(message: String) {
        _toastEvents.tryEmit(
            ToastMessage(
                message = message,
                type = ToastType.ERROR,
            )
        )
    }

    fun showWarning(message: String) {
        _toastEvents.tryEmit(
            ToastMessage(
                message = message,
                type = ToastType.WARNING,
            )
        )
    }

    fun showInfo(message: String) {
        _toastEvents.tryEmit(
            ToastMessage(
                message = message,
                type = ToastType.INFO,
            )
        )
    }
}
