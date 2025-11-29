package com.example.kmp_template.shared_client.settings.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_template.shared_client.settings.presentation.SettingsAction
import com.example.kmp_template.shared_client.settings.presentation.SettingsSize
import com.example.kmp_template.shared_client.settings.presentation.SettingsState
import com.example.kmp_template.shared_client.settings.presentation.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
    headerSize: SettingsSize.HeaderSize = SettingsSize.header,
    listSize: SettingsSize.ListSize = SettingsSize.list,
    formSize: SettingsSize.FormSize = SettingsSize.form,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction,
        headerSize = headerSize,
        listSize = listSize,
        formSize = formSize,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    headerSize: SettingsSize.HeaderSize = SettingsSize.header,
    listSize: SettingsSize.ListSize = SettingsSize.list,
    formSize: SettingsSize.FormSize = SettingsSize.form,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(formSize.fieldSpacing),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerSize.height)
                .padding(horizontal = headerSize.spacing),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(headerSize.spacing),
        ) {
            IconButton(
                onClick = { onAction(SettingsAction.OnBackClicked) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(headerSize.backIconSize),
                )
            }

            Text(
                text = "Settings",
                style = headerSize.titleTextStyle,
            )
        }

        LoadedSettingsList(
            state = state,
            onAction = onAction,
            listSize = listSize,
            formSize = formSize,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        NewSetting(
            state = state,
            onAction = onAction,
            formSize = formSize,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            enabled = state.isFormValid,
            onClick = { onAction(SettingsAction.OnSaveSettings) },
            modifier = Modifier.height(formSize.buttonHeight),
        ) {
            Text(
                text = "Save Settings",
                style = formSize.buttonTextStyle,
            )
        }
    }
}


@Composable
private fun LoadedSettingsList(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    listSize: SettingsSize.ListSize = SettingsSize.list,
    formSize: SettingsSize.FormSize = SettingsSize.form,
) {
    Card(modifier) {
        Column(modifier = Modifier.padding(listSize.itemPadding)) {

            Text(
                text = "Existing Settings",
                style = listSize.nameTextStyle,
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(listSize.spacing)) {
                items(state.settingsForm.orEmpty()) { form ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = form.valueField.value,
                            onValueChange = { newValue ->
                                onAction(
                                    SettingsAction.LoadedSetting.OnValueChanged(
                                        form.copy(valueField = form.valueField.copy(value = newValue))
                                    )
                                )
                            },
                            label = { Text(form.key, style = formSize.labelTextStyle) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = formSize.inputTextStyle,
                            isError = form.valueField.isInvalid,
                            supportingText = {
                                if (form.valueField.isInvalid) {
                                    Column {
                                        form.valueField.errors.forEach { error ->
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
                                modifier = Modifier.height(formSize.buttonHeight),
                            ) {
                                Text("Delete", style = formSize.buttonTextStyle)
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
    formSize: SettingsSize.FormSize = SettingsSize.form,
) {
    Card(modifier) {
        Column(
            modifier = Modifier.padding(formSize.cardPadding),
            verticalArrangement = Arrangement.spacedBy(formSize.fieldSpacing),
        ) {

            Text("Add New Setting", style = formSize.labelTextStyle)

            state.newSettingForm.let {
                OutlinedTextField(
                    value = it.keyField.value.orEmpty(),
                    onValueChange = { newKey ->
                        onAction(SettingsAction.NewSetting.OnKeyChanged(newKey))
                    },
                    label = { Text("New Setting Key", style = formSize.labelTextStyle) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = formSize.inputTextStyle,
                    isError = it.keyField.isInvalid,
                    supportingText = {
                        if (it.keyField.isInvalid) {
                            Column {
                                it.keyField.errors.forEach { error ->
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
                    value = it.valueField.value.orEmpty(),
                    onValueChange = { newValue ->
                        onAction(SettingsAction.NewSetting.OnValueChanged(newValue))
                    },
                    label = { Text("New Setting Value", style = formSize.labelTextStyle) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = formSize.inputTextStyle,
                    isError = it.valueField.isInvalid,
                    supportingText = {
                        if (it.valueField.isInvalid) {
                            Column {
                                it.valueField.errors.forEach { error ->
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
