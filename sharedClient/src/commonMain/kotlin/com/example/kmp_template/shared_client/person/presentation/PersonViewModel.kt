package com.example.kmp_template.shared_client.person.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.core.result.onError
import com.example.kmp_template.core.result.onSuccess
import com.example.kmp_template.shared_client.core.StringValue
import com.example.kmp_template.shared_client.core.navigation.NavigationService
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.person.domain.repository.PersonRepository
import com.example.kmp_template.shared_client.person.presentation.mapper.toDomain
import com.example.kmp_template.shared_client.person.presentation.mapper.toPersonForm
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PersonViewModel(
    private val personRepo: PersonRepository,
    private val nav: NavigationService,
    private val toastService: ToastService,
) : ViewModel() {
    companion object {
        const val TAG = "PersonViewModel"
    }

    private val _state = MutableStateFlow(PersonState())
    val state = _state.asStateFlow()
        .onStart {
            observeLocalPeople()
            observeFormForValidation()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds),
            _state.value
        )

    fun onAction(action: PersonAction) {
        Log.tag(TAG).v { "action received $action" }

        when (action) {
            PersonAction.OnBackClicked -> nav.back()

            PersonAction.OnLoadRemotePeopleClicked -> {
                viewModelScope.launch {
                    personRepo.getAllRemotePeople()
                        .onError { toastService.showError(StringValue.Raw("Unable to fetch remote people")) }
                        .onSuccess { fetchedPeople ->
                            _state.update { old ->
                                old.copy(
                                    remotePersonForm = fetchedPeople
                                        .map { it.toPersonForm() }
                                        .toImmutableList(),
                                )
                            }
                        }
                }
            }

            is PersonAction.LoadedPerson.OnDeletePerson -> {
                viewModelScope.launch {
                    personRepo.deletePerson(action.form.toDomain())
                        .onError { toastService.showError(StringValue.Raw("Failed to delete person. Please try again.")) }
                        .onSuccess { toastService.showSuccess(StringValue.Raw("Person deleted successfully")) }
                }
            }

            is PersonAction.LoadedPerson.OnNameChanged -> {
                val currentState = _state.value

                val updatedLocalPeople = currentState.localPersonForm
                    ?.map {
                        if (it.id == action.form.id) {
                            it.copy(nameField = action.form.nameField)
                        } else {
                            it
                        }
                    }

                val updatedRemotePeople = currentState.remotePersonForm
                    ?.map {
                        if (it.id == action.form.id) {
                            it.copy(nameField = action.form.nameField)
                        } else {
                            it
                        }
                    }

                _state.update { old ->
                    old.copy(
                        localPersonForm = updatedLocalPeople?.toImmutableList(),
                        remotePersonForm = updatedRemotePeople?.toImmutableList(),
                    )
                }
            }

            is PersonAction.LoadedPerson.OnSavePerson -> {
                viewModelScope.launch {
                    personRepo.savePerson(action.form.toDomain())
                        .onError { toastService.showError(StringValue.Raw("Unable to save person")) }
                        .onSuccess { toastService.showInfo(StringValue.Raw("Saved person")) }
                }
            }

            is PersonAction.NewPerson.OnNameChanged -> {
                val currentState = _state.value

                val updatedNewForm = currentState.newPersonForm.copy(
                    nameField = currentState.newPersonForm.nameField.copy(
                        value = action.name,
                    )
                )

                _state.update { old ->
                    old.copy(newPersonForm = updatedNewForm)
                }
            }

            is PersonAction.NewPerson.OnLocalChanged -> {
                val currentState = _state.value

                val updatedNewForm = currentState.newPersonForm.copy(
                    localField = currentState.newPersonForm.localField.copy(
                        value = action.local,
                    )
                )

                _state.update { old ->
                    old.copy(newPersonForm = updatedNewForm)
                }
            }

            PersonAction.NewPerson.OnSavePerson -> {
                viewModelScope.launch {
                    val newPerson = _state.value.newPersonForm.toDomain()
                    if (newPerson == null) {
                        _state.update { old ->
                            old.copy(
                                newPersonForm = old.newPersonForm.copy(
                                    nameField = old.newPersonForm.nameField.copy(
                                        errors = persistentListOf(StringValue.Raw("Can not save new person")),
                                    ),
                                    localField = old.newPersonForm.localField.copy(
                                        errors = persistentListOf(StringValue.Raw("Can not save new person")),
                                    )
                                )
                            )
                        }

                    } else {
                        personRepo.savePerson(newPerson)
                            .onError { toastService.showError(StringValue.Raw("Unable to save person")) }
                            .onSuccess { toastService.showInfo(StringValue.Raw("Saved person")) }
                    }
                }
            }
        }
    }

    private fun observeLocalPeople() {
        personRepo.getAllLocalPeople()
            .onError { Log.tag(TAG).d { "Unable to get local flows" } }
            .onSuccess { peopleFlow ->
                peopleFlow
                    .onEach { people ->
                        Log.tag(TAG).v { "local people changed" }
                        val peopleForm = people
                            .map { it.toPersonForm() }
                            .toImmutableList()

                        _state.update { old ->
                            old.copy(localPersonForm = peopleForm)
                        }
                    }
                    .launchIn(viewModelScope)
            }
    }

    @OptIn(FlowPreview::class)
    private fun observeFormForValidation() {
        state
            .map { Triple(it.localPersonForm, it.remotePersonForm, it.newPersonForm) }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .onEach { (localPersonForm, remotePersonForm, newPersonForm) ->
                Log.tag(TAG).v { "validating Form" }

                val validatedNewForm = newPersonForm.let { form ->
                    val nameErrors = mutableListOf<StringValue>()
                    if (newPersonForm.nameField.value?.isBlank() == true) { // null means untouched field
                        nameErrors.add(StringValue.Raw("Name cannot be empty"))
                    }

                    if (newPersonForm.localField.value ?: false) {
                        val existsLocal = localPersonForm.orEmpty()
                            .any { it.nameField.value == newPersonForm.nameField.value }
                        if (existsLocal) {
                            nameErrors.add(StringValue.Raw("Name already exists in local"))
                        }

                    } else {
                        val existsRemote = remotePersonForm.orEmpty()
                            .any { it.nameField.value == newPersonForm.nameField.value }
                        if (existsRemote) {
                            nameErrors.add(StringValue.Raw("Name already exists in remote"))
                        }
                    }

                    form.copy(
                        nameField = form.nameField.copy(errors = nameErrors.toImmutableList())
                    )
                }

                val validatedLocalForm  = localPersonForm?.map { form ->
                    val nameErrors = mutableListOf<StringValue>()
                    if (form.nameField.value.isBlank()) {
                        nameErrors.add(StringValue.Raw("Name cannot be empty"))
                    }

                    form.copy(nameField = form.nameField.copy(errors = nameErrors.toImmutableList()))
                }

                val validatedRemoteForm  = remotePersonForm?.map { form ->
                    val nameErrors = mutableListOf<StringValue>()
                    if (form.nameField.value.isBlank()) {
                        nameErrors.add(StringValue.Raw("Name cannot be empty"))
                    }

                    form.copy(nameField = form.nameField.copy(errors = nameErrors.toImmutableList()))
                }


                val isFormValid = validatedNewForm.nameField.isValid &&
                        validatedNewForm.localField.isValid &&
                        validatedLocalForm.orEmpty().all { it.nameField.isValid } &&
                        validatedRemoteForm.orEmpty().all { it.nameField.isValid }

                _state.update { old ->
                    old.copy(
                        newPersonForm = validatedNewForm,
                        localPersonForm = validatedLocalForm?.toImmutableList(),
                        remotePersonForm = validatedRemoteForm?.toImmutableList(),
                        isFormValid = isFormValid,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
