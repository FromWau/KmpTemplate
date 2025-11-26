package com.example.kmp_template.shared_client.core.presentation.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ToastHost(
    modifier: Modifier = Modifier,
    toastService: ToastService = koinInject(),
    colors: ToastColors = ToastDefaults.colors(),
    shape: Shape = ToastDefaults.shape,
) {
    var currentToast by remember { mutableStateOf<ToastMessage?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        toastService.toastEvents.collect { toast ->
            currentToast = toast
            visible = true

            // Auto-hide after duration
            delay(toast.duration)
            visible = false

            // Clear toast after animation
            delay(300.milliseconds) // Animation duration
            currentToast = null
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = visible && currentToast != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        ) {
            currentToast?.let { toast ->
                Toast(
                    message = toast.message,
                    type = toast.type,
                    colors = colors,
                    shape = shape,
                )
            }
        }
    }
}

@Composable
private fun Toast(
    message: String,
    type: ToastType,
    modifier: Modifier = Modifier,
    colors: ToastColors = ToastDefaults.colors(),
    shape: Shape = ToastDefaults.shape,
) {
    val containerColor = when (type) {
        ToastType.SUCCESS -> colors.successContainerColor
        ToastType.ERROR -> colors.errorContainerColor
        ToastType.WARNING -> colors.warningContainerColor
        ToastType.INFO -> colors.infoContainerColor
    }
    val contentColor = when (type) {
        ToastType.SUCCESS -> colors.successContentColor
        ToastType.ERROR -> colors.errorContentColor
        ToastType.WARNING -> colors.warningContentColor
        ToastType.INFO -> colors.infoContentColor
    }

    Box(
        modifier = modifier
            .padding(ToastDefaults.margin)
            .background(
                color = containerColor,
                shape = shape,
            )
            .padding(
                horizontal = ToastDefaults.horizontalPadding,
                vertical = ToastDefaults.verticalPadding,
            ),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}
