package com.example.kmp_template.shared_client.setting.presentation.composable

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
import com.example.kmp_template.shared_client.setting.presentation.SettingAction
import com.example.kmp_template.shared_client.setting.presentation.SettingSize
import com.example.kmp_template.shared_client.setting.presentation.SettingState
import com.example.kmp_template.shared_client.setting.presentation.SettingViewModel
import com.example.kmp_template.shared_client.theme.DeviceType
import com.example.kmp_template.shared_client.theme.LocalDeviceType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = koinViewModel<SettingViewModel>(),
    headerSize: SettingSize.HeaderSize = SettingSize.header,
    listSize: SettingSize.ListSize = SettingSize.list,
    formSize: SettingSize.FormSize = SettingSize.form,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingContent(
        state = state,
        onAction = viewModel::onAction,
        headerSize = headerSize,
        listSize = listSize,
        formSize = formSize,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingContent(
    state: SettingState,
    onAction: (SettingAction) -> Unit,
    modifier: Modifier = Modifier,
    headerSize: SettingSize.HeaderSize = SettingSize.header,
    listSize: SettingSize.ListSize = SettingSize.list,
    formSize: SettingSize.FormSize = SettingSize.form,
) {
    val deviceType = LocalDeviceType.current

    Column(
        modifier = modifier,
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
            if (deviceType == DeviceType.MOBILE_PORTRAIT || deviceType == DeviceType.TABLET_PORTRAIT) {
                IconButton(
                    onClick = { onAction(SettingAction.OnBackClicked) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(headerSize.backIconSize),
                    )
                }
            }

            Text(
                text = "Setting",
                style = headerSize.titleTextStyle,
            )
        }

        LoadedSettingList(
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
            onClick = { onAction(SettingAction.OnSaveSetting) },
            modifier = Modifier.height(formSize.buttonHeight),
        ) {
            Text(
                text = "Save Setting",
                style = formSize.buttonTextStyle,
            )
        }
    }
}


@Composable
private fun LoadedSettingList(
    state: SettingState,
    onAction: (SettingAction) -> Unit,
    modifier: Modifier = Modifier,
    listSize: SettingSize.ListSize = SettingSize.list,
    formSize: SettingSize.FormSize = SettingSize.form,
) {
    Card(modifier) {
        Column(modifier = Modifier.padding(listSize.itemPadding)) {

            Text(
                text = "Existing Setting",
                style = listSize.nameTextStyle,
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(listSize.spacing)) {
                items(state.settingForm.orEmpty()) { form ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = form.valueField.value,
                            onValueChange = { newValue ->
                                onAction(
                                    SettingAction.LoadedSetting.OnValueChanged(
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
                                        SettingAction.LoadedSetting.OnDeleteSetting(
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
    state: SettingState,
    onAction: (SettingAction) -> Unit,
    modifier: Modifier = Modifier,
    formSize: SettingSize.FormSize = SettingSize.form,
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
                        onAction(SettingAction.NewSetting.OnKeyChanged(newKey))
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
                        onAction(SettingAction.NewSetting.OnValueChanged(newValue))
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
