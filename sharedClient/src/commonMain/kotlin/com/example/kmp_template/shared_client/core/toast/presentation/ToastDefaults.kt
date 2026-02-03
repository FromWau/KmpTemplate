package com.example.kmp_template.shared_client.core.toast.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ToastDefaults {
    val shape: Shape = RoundedCornerShape(8.dp)

    val horizontalPadding: Dp = 16.dp

    val verticalPadding: Dp = 12.dp

    @ReadOnlyComposable
    @Composable
    fun colors(
        successContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
        successContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        errorContainerColor: Color = MaterialTheme.colorScheme.errorContainer,
        errorContentColor: Color = MaterialTheme.colorScheme.onErrorContainer,
        warningContainerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
        warningContentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
        infoContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        infoContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    ) = ToastColors(
        successContainerColor = successContainerColor,
        successContentColor = successContentColor,
        errorContainerColor = errorContainerColor,
        errorContentColor = errorContentColor,
        warningContainerColor = warningContainerColor,
        warningContentColor = warningContentColor,
        infoContainerColor = infoContainerColor,
        infoContentColor = infoContentColor,
    )
}

@Immutable
data class ToastColors(
    val successContainerColor: Color,
    val successContentColor: Color,
    val errorContainerColor: Color,
    val errorContentColor: Color,
    val warningContainerColor: Color,
    val warningContentColor: Color,
    val infoContainerColor: Color,
    val infoContentColor: Color,
)
