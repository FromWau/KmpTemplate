package com.example.kmp_template.shared_client.person.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kmp_template.shared_client.theme.DeviceType
import com.example.kmp_template.shared_client.theme.LocalDeviceType

object PersonSize {

    @Immutable
    data class HeaderSize(
        val height: Dp,
        val backIconSize: Dp,
        val titleTextStyle: TextStyle,
        val spacing: Dp,
    )

    @Immutable
    data class ListSize(
        val itemHeight: Dp,
        val itemPadding: Dp,
        val nameTextStyle: TextStyle,
        val valueTextStyle: TextStyle,
        val spacing: Dp,
    )

    @Immutable
    data class FormSize(
        val cardPadding: Dp,
        val fieldSpacing: Dp,
        val labelTextStyle: TextStyle,
        val inputTextStyle: TextStyle,
        val buttonHeight: Dp,
        val buttonTextStyle: TextStyle,
    )

    val header: HeaderSize
        @Composable @ReadOnlyComposable get() {
            val deviceType = LocalDeviceType.current

            return when (deviceType) {
                DeviceType.MOBILE_PORTRAIT -> HeaderSize(
                    height = 56.dp,
                    backIconSize = 24.dp,
                    titleTextStyle = MaterialTheme.typography.titleLarge,
                    spacing = 8.dp,
                )

                DeviceType.MOBILE_LANDSCAPE -> HeaderSize(
                    height = 48.dp,
                    backIconSize = 24.dp,
                    titleTextStyle = MaterialTheme.typography.titleLarge,
                    spacing = 8.dp,
                )

                DeviceType.TABLET_PORTRAIT -> HeaderSize(
                    height = 64.dp,
                    backIconSize = 28.dp,
                    titleTextStyle = MaterialTheme.typography.headlineSmall,
                    spacing = 12.dp,
                )

                DeviceType.TABLET_LANDSCAPE -> HeaderSize(
                    height = 64.dp,
                    backIconSize = 28.dp,
                    titleTextStyle = MaterialTheme.typography.headlineSmall,
                    spacing = 12.dp,
                )

                DeviceType.DESKTOP -> HeaderSize(
                    height = 72.dp,
                    backIconSize = 32.dp,
                    titleTextStyle = MaterialTheme.typography.headlineMedium,
                    spacing = 16.dp,
                )
            }
        }

    val list: ListSize
        @Composable @ReadOnlyComposable get() {
            val deviceType = LocalDeviceType.current

            return when (deviceType) {
                DeviceType.MOBILE_PORTRAIT -> ListSize(
                    itemHeight = 72.dp,
                    itemPadding = 16.dp,
                    nameTextStyle = MaterialTheme.typography.bodyLarge,
                    valueTextStyle = MaterialTheme.typography.bodyMedium,
                    spacing = 4.dp,
                )

                DeviceType.MOBILE_LANDSCAPE -> ListSize(
                    itemHeight = 64.dp,
                    itemPadding = 16.dp,
                    nameTextStyle = MaterialTheme.typography.bodyLarge,
                    valueTextStyle = MaterialTheme.typography.bodyMedium,
                    spacing = 4.dp,
                )

                DeviceType.TABLET_PORTRAIT -> ListSize(
                    itemHeight = 80.dp,
                    itemPadding = 20.dp,
                    nameTextStyle = MaterialTheme.typography.titleMedium,
                    valueTextStyle = MaterialTheme.typography.bodyLarge,
                    spacing = 6.dp,
                )

                DeviceType.TABLET_LANDSCAPE -> ListSize(
                    itemHeight = 80.dp,
                    itemPadding = 20.dp,
                    nameTextStyle = MaterialTheme.typography.titleMedium,
                    valueTextStyle = MaterialTheme.typography.bodyLarge,
                    spacing = 6.dp,
                )

                DeviceType.DESKTOP -> ListSize(
                    itemHeight = 88.dp,
                    itemPadding = 24.dp,
                    nameTextStyle = MaterialTheme.typography.titleLarge,
                    valueTextStyle = MaterialTheme.typography.bodyLarge,
                    spacing = 8.dp,
                )
            }
        }

    val form: FormSize
        @Composable @ReadOnlyComposable get() {
            val deviceType = LocalDeviceType.current

            return when (deviceType) {
                DeviceType.MOBILE_PORTRAIT -> FormSize(
                    cardPadding = 16.dp,
                    fieldSpacing = 16.dp,
                    labelTextStyle = MaterialTheme.typography.labelLarge,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    buttonHeight = 48.dp,
                    buttonTextStyle = MaterialTheme.typography.labelLarge,
                )

                DeviceType.MOBILE_LANDSCAPE -> FormSize(
                    cardPadding = 16.dp,
                    fieldSpacing = 12.dp,
                    labelTextStyle = MaterialTheme.typography.labelLarge,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    buttonHeight = 44.dp,
                    buttonTextStyle = MaterialTheme.typography.labelLarge,
                )

                DeviceType.TABLET_PORTRAIT -> FormSize(
                    cardPadding = 20.dp,
                    fieldSpacing = 20.dp,
                    labelTextStyle = MaterialTheme.typography.labelLarge,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    buttonHeight = 52.dp,
                    buttonTextStyle = MaterialTheme.typography.titleMedium,
                )

                DeviceType.TABLET_LANDSCAPE -> FormSize(
                    cardPadding = 20.dp,
                    fieldSpacing = 20.dp,
                    labelTextStyle = MaterialTheme.typography.labelLarge,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    buttonHeight = 52.dp,
                    buttonTextStyle = MaterialTheme.typography.titleMedium,
                )

                DeviceType.DESKTOP -> FormSize(
                    cardPadding = 24.dp,
                    fieldSpacing = 24.dp,
                    labelTextStyle = MaterialTheme.typography.titleMedium,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    buttonHeight = 56.dp,
                    buttonTextStyle = MaterialTheme.typography.titleMedium,
                )
            }
        }
}
