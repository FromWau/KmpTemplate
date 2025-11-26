package com.example.kmp_template.shared_client.core.presentation.toast

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ToastMessage(
    val message: String,
    val duration: Duration = 3.seconds,
    val type: ToastType = ToastType.INFO,
)

enum class ToastType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO,
}
