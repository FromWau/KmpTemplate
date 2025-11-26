package com.example.kmp_template.shared_client.settings.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_template.shared_client.settings.presentation.SettingsAction
import com.example.kmp_template.shared_client.settings.presentation.SettingsState
import com.example.kmp_template.shared_client.settings.presentation.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row {
            TextButton(
                modifier = Modifier.weight(3f),
                onClick = { onAction(SettingsAction.OnBackClicked) },
            ) {
                Text("Back")
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(7f),
            )
        }

        LoadedSettingsList(
            state = state,
            onAction = onAction,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        NewSetting(
            state = state,
            onAction = onAction,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            enabled = state.isFormValid,
            onClick = { onAction(SettingsAction.OnSaveSettings) },
        ) {
            Text("Save Settings")
        }
    }
}


@Composable
private fun LoadedSettingsList(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier) {
        Column(modifier = Modifier.padding(8.dp)) {

            Text("Existing Settings", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(state.settingsForm.orEmpty()) { form ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = form.value,
                            onValueChange = { newValue ->
                                onAction(
                                    SettingsAction.LoadedSetting.OnValueChanged(
                                        form.copy(value = newValue)
                                    )
                                )
                            },
                            label = { Text(form.key) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = form.valueErrors.isNotEmpty(),
                            supportingText = {
                                if (form.valueErrors.isNotEmpty()) {
                                    Column {
                                        form.valueErrors.forEach { error ->
                                            Text(
                                                text = error.asString(),
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    }
                                }
                            },
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Button(
                                onClick = {
                                    onAction(
                                        SettingsAction.LoadedSetting.OnDeleteSetting(
                                            form
                                        )
                                    )
                                },
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewSetting(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier) {
        Column(modifier = Modifier.padding(8.dp)) {

            Text("Add New Setting", style = MaterialTheme.typography.titleMedium)

            state.newSettingForm.let {
                OutlinedTextField(
                    value = it.key.orEmpty(),
                    onValueChange = { newKey ->
                        onAction(SettingsAction.NewSetting.OnKeyChanged(newKey))
                    },
                    label = { Text("New Setting Key") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = it.keyErrors.isNotEmpty(),
                    supportingText = {
                        if (it.keyErrors.isNotEmpty()) {
                            Column {
                                it.keyErrors.forEach { error ->
                                    Text(
                                        text = error.asString(),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                    },
                )

                OutlinedTextField(
                    value = it.value.orEmpty(),
                    onValueChange = { newValue ->
                        onAction(SettingsAction.NewSetting.OnValueChanged(newValue))
                    },
                    label = { Text("New Setting Value") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = it.valueErrors.isNotEmpty(),
                    supportingText = {
                        if (it.valueErrors.isNotEmpty()) {
                            Column {
                                it.valueErrors.forEach { error ->
                                    Text(
                                        text = error.asString(),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}
