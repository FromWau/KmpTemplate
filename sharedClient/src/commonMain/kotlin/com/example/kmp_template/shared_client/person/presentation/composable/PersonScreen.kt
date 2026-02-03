package com.example.kmp_template.shared_client.person.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_template.shared_client.person.presentation.PersonAction
import com.example.kmp_template.shared_client.person.presentation.PersonSize
import com.example.kmp_template.shared_client.person.presentation.PersonState
import com.example.kmp_template.shared_client.person.presentation.PersonViewModel
import com.example.kmp_template.shared_client.theme.DeviceType
import com.example.kmp_template.shared_client.theme.LocalDeviceType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PersonScreen(
    viewModel: PersonViewModel = koinViewModel<PersonViewModel>(),
    headerSize: PersonSize.HeaderSize = PersonSize.header,
    listSize: PersonSize.ListSize = PersonSize.list,
    formSize: PersonSize.FormSize = PersonSize.form,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PersonContent(
        state = state,
        onAction = viewModel::onAction,
        headerSize = headerSize,
        listSize = listSize,
        formSize = formSize,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonContent(
    state: PersonState,
    onAction: (PersonAction) -> Unit,
    modifier: Modifier = Modifier,
    headerSize: PersonSize.HeaderSize = PersonSize.header,
    listSize: PersonSize.ListSize = PersonSize.list,
    formSize: PersonSize.FormSize = PersonSize.form,
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
                    onClick = { onAction(PersonAction.OnBackClicked) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(headerSize.backIconSize),
                    )
                }
            }

            Text(
                text = "Person",
                style = headerSize.titleTextStyle,
            )
        }

        LoadedPeopleFormList(
            state = state,
            onAction = onAction,
            listSize = listSize,
            formSize = formSize,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        NewPersonForm(
            state = state,
            onAction = onAction,
            formSize = formSize,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
    }
}


@Composable
private fun LoadedPeopleFormList(
    state: PersonState,
    onAction: (PersonAction) -> Unit,
    modifier: Modifier = Modifier,
    listSize: PersonSize.ListSize = PersonSize.list,
    formSize: PersonSize.FormSize = PersonSize.form,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(listSize.spacing),
    ) {
        Card(Modifier.weight(1f).fillMaxWidth()) {
            Column(modifier = Modifier.padding(listSize.itemPadding)) {
                Text(
                    text = "Local People (${state.localPersonForm?.size ?: 0})",
                    style = formSize.labelTextStyle,
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(listSize.spacing)) {
                    items(state.localPersonForm.orEmpty()) { form ->
                        LoadedPersonFormItem(
                            form = form,
                            onAction = onAction,
                            formSize = formSize,
                        )
                    }
                }
            }
        }

        Card(Modifier.weight(1f).fillMaxWidth()) {
            Column(modifier = Modifier.padding(listSize.itemPadding)) {
                Row {
                    Text(
                        text = "Remote People (${state.remotePersonForm?.size ?: 0})",
                        style = formSize.labelTextStyle,
                    )

                    TextButton(
                        onClick = { onAction(PersonAction.OnLoadRemotePeopleClicked) },
                    ) {
                        Text("Load Remote", style = formSize.labelTextStyle)
                    }
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(listSize.spacing)) {
                    items(state.remotePersonForm.orEmpty()) { form ->
                        LoadedPersonFormItem(
                            form = form,
                            onAction = onAction,
                            formSize = formSize,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadedPersonFormItem(
    form: PersonState.PersonForm,
    onAction: (PersonAction) -> Unit,
    formSize: PersonSize.FormSize = PersonSize.form,
) {
    Column {
        OutlinedTextField(
            value = form.nameField.value,
            onValueChange = { newValue ->
                onAction(
                    PersonAction.LoadedPerson.OnNameChanged(
                        form.copy(nameField = form.nameField.copy(value = newValue))
                    )
                )
            },
            label = { Text(form.id.toString(), style = formSize.labelTextStyle) },
            leadingIcon = {
                if (form.local) {
                    Text("Local", style = formSize.inputTextStyle)
                } else {
                    Text("Remote", style = formSize.inputTextStyle)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = formSize.inputTextStyle,
            isError = form.nameField.isInvalid,
            supportingText = {
                if (form.nameField.isInvalid) {
                    Column {
                        form.nameField.errors.forEach { error ->
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
                        PersonAction.LoadedPerson.OnDeletePerson(
                            form
                        )
                    )
                },
                modifier = Modifier.height(formSize.buttonHeight),
            ) {
                Text("Delete", style = formSize.buttonTextStyle)
            }

            Button(
                onClick = {
                    onAction(
                        PersonAction.LoadedPerson.OnSavePerson(
                            form
                        )
                    )
                },
                modifier = Modifier.height(formSize.buttonHeight),
            ) {
                Text("Save", style = formSize.buttonTextStyle)
            }
        }
    }
}


@Composable
private fun NewPersonForm(
    state: PersonState,
    onAction: (PersonAction) -> Unit,
    modifier: Modifier = Modifier,
    formSize: PersonSize.FormSize = PersonSize.form,
) {
    Card(modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(formSize.cardPadding),
            verticalArrangement = Arrangement.spacedBy(formSize.fieldSpacing),
        ) {

            Text("Add New Person", style = formSize.labelTextStyle)

            state.newPersonForm.let {
                OutlinedTextField(
                    value = it.nameField.value.orEmpty(),
                    onValueChange = { newKey ->
                        onAction(PersonAction.NewPerson.OnNameChanged(newKey))
                    },
                    label = { Text("New Person Name", style = formSize.labelTextStyle) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = formSize.inputTextStyle,
                    isError = it.nameField.isInvalid,
                    supportingText = {
                        if (it.nameField.isInvalid) {
                            Column {
                                it.nameField.errors.forEach { error ->
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

                Row {
                    Text("Store Local?", style = formSize.inputTextStyle)
                    Checkbox(
                        checked = it.localField.value ?: false,
                        onCheckedChange = { isChecked ->
                            onAction(PersonAction.NewPerson.OnLocalChanged(isChecked))
                        },
                    )
                }
            }

            Button(
                onClick = {
                    onAction(PersonAction.NewPerson.OnSavePerson)
                },
                modifier = Modifier.height(formSize.buttonHeight),
            ) {
                Text("Save", style = formSize.buttonTextStyle)
            }
        }
    }
}
