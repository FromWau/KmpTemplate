package com.example.kmp_template.shared_client.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_template.shared_client.person.presentation.composable.PersonScreen
import com.example.kmp_template.shared_client.theme.DeviceType
import com.example.kmp_template.shared_client.theme.LocalDeviceType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
    personContent: @Composable (Modifier) -> Unit = { PersonScreen() },
) {
    val deviceType = LocalDeviceType.current

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Welcome to the Home Screen!")

            Text(
                text = "Device Type: $deviceType",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            when (deviceType) {
                DeviceType.MOBILE_PORTRAIT,
                DeviceType.TABLET_PORTRAIT,
                    -> {
                    Button(
                        onClick = { onAction(HomeAction.OnPersonClicked) }
                    ) {
                        Text("Person")
                    }
                }

                else -> {}
            }
        }

        when (deviceType) {
            DeviceType.MOBILE_LANDSCAPE,
            DeviceType.TABLET_LANDSCAPE,
            DeviceType.DESKTOP,
                -> {
                    Column(
                        modifier = Modifier.fillMaxHeight().weight(1f)
                    ) {
                        personContent(Modifier)
                    }
            }

            else -> {}
        }
    }
}
