package com.example.kmp_template.shared_client.core.toast.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.kmp_template.shared_client.core.StringValue
import com.example.kmp_template.shared_client.core.toast.ToastMessage
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.core.toast.ToastType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

@Composable
fun ToastHost(
    modifier: Modifier = Modifier,
    toastService: ToastService = koinInject(),
    colors: ToastColors = ToastDefaults.colors(),
    shape: Shape = ToastDefaults.shape,
) {
    val toasts = remember { mutableStateMapOf<Uuid, ToastMessage>() }
    val visibilityMap = remember { mutableStateMapOf<Uuid, Boolean>() }

    LaunchedEffect(Unit) {
        toastService.toastEvents.collect { toast ->
            toasts[toast.id] = toast
            visibilityMap[toast.id] = true

            launch {
                // Auto-hide after duration
                delay(toast.duration)
                visibilityMap[toast.id] = false

                // Clear toast after animation
                delay(300.milliseconds) // Animation duration
                toasts.remove(toast.id)
                visibilityMap.remove(toast.id)
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            toasts.values.forEach { toast ->
                AnimatedVisibility(
                    visible = visibilityMap[toast.id] == true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                ) {
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
}

@Composable
private fun Toast(
    message: StringValue,
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
            text = message.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}
