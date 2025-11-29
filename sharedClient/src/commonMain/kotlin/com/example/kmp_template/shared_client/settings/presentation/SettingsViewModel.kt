package com.example.kmp_template.shared_client.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_client.core.presentation.StringValue
import com.example.kmp_template.shared_client.core.presentation.navigation.NavigationService
import com.example.kmp_template.shared_client.core.presentation.toast.ToastService
import com.example.kmp_template.shared_client.settings.domain.model.Settings
import com.example.kmp_template.shared_client.settings.domain.repository.SettingsRepository
import com.example.kmp_template.shared_client.settings.presentation.mapper.toSetting
import com.example.kmp_template.shared_client.settings.presentation.mapper.toSettingsForm
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

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val nav: NavigationService,
    private val toastService: ToastService,
) : ViewModel() {
    companion object {
        const val TAG = "SettingsViewModel"
    }

    private val _state = MutableStateFlow(SettingsState())

    @OptIn(FlowPreview::class)
    val state = _state.asStateFlow()
        .onStart {
            observeSettings()
            observeFormForValidation()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds),
            _state.value
        )

    fun onAction(action: SettingsAction) {
        Log.tag(TAG).v { "action received $action" }

        when (action) {
            SettingsAction.OnBackClicked -> nav.back()

            SettingsAction.OnSaveSettings -> {
                val currentState = _state.value

                if (!currentState.isFormValid) {
                    Log.tag(TAG).v { "form is not valid, not saving" }
                    return
                }

                val currentSettings = currentState.settingsForm
                    .orEmpty()
                    .map { form -> form.toSetting() }

                val newSetting = currentState.newSettingForm.toSetting()

                val allSettings = (currentSettings + newSetting).filterNotNull()

                viewModelScope.launch {
                    val result = settingsRepository.upsertSettings(Settings(allSettings))

                    if (result) {
                        toastService.showSuccess("Settings saved successfully")
                        _state.update { old ->
                            old.copy(newSettingForm = SettingsState.NewForm())
                        }
                    } else {
                        toastService.showError("Failed to save settings. Please try again.")
                    }
                }
            }

            is SettingsAction.LoadedSetting.OnDeleteSetting -> {
                viewModelScope.launch {
                    val result = settingsRepository.deleteSetting(action.form.toSetting())

                    if (result) {
                        toastService.showSuccess("Setting deleted successfully")
                    } else {
                        toastService.showError("Failed to delete setting. Please try again.")
                    }
                }
            }

            is SettingsAction.LoadedSetting.OnValueChanged -> {
                val currentState = _state.value

                val updatedSettings = currentState.settingsForm
                    ?.map {
                        if (it.key == action.form.key) {
                            it.copy(valueField = action.form.valueField)
                        } else {
                            it
                        }
                    }

                _state.update { old ->
                    old.copy(settingsForm = updatedSettings?.toImmutableList())
                }
            }

            is SettingsAction.NewSetting.OnKeyChanged -> {
                val currentState = _state.value

                val updatedNewForm = currentState.newSettingForm.copy(
                    keyField = currentState.newSettingForm.keyField.copy(
                        value = action.key,
                    )
                )

                _state.update { old ->
                    old.copy(newSettingForm = updatedNewForm)
                }
            }

            is SettingsAction.NewSetting.OnValueChanged -> {
                val currentState = _state.value

                val updatedNewForm = currentState.newSettingForm.copy(
                    valueField = currentState.newSettingForm.valueField.copy(
                        value = action.value,
                    )
                )

                _state.update { old ->
                    old.copy(newSettingForm = updatedNewForm)
                }
            }
        }
    }

    private fun observeSettings() {
        settingsRepository.getSettingsFlow()
            .onEach { settings ->
                Log.tag(TAG).v { "settings changed" }
                val settingsForm = settings.toSettingsForm()

                _state.update { old ->
                    old.copy(settingsForm = settingsForm)
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun observeFormForValidation() {
        state
            .map { it.settingsForm to it.newSettingForm }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .onEach { (settingsForm, newSettingForm) ->
                Log.tag(TAG).v { "validating Form" }

                val validatedNewForm = newSettingForm.let { form ->
                    val keyErrors = mutableListOf<StringValue>()
                    if (newSettingForm.keyField.value?.isBlank() == true) { // null means untouched field
                        keyErrors.add(StringValue.Raw("Key cannot be empty"))
                    }
                    val keyAlreadyExists: Boolean = settingsForm.orEmpty().any {
                        it.key == newSettingForm.keyField.value
                    }
                    if (keyAlreadyExists) {
                        keyErrors.add(StringValue.Raw("Key already exists"))
                    }

                    val valueErrors = mutableListOf<StringValue>()
                    if (newSettingForm.valueField.value?.isBlank() == true) { // null means untouched field
                        valueErrors.add(StringValue.Raw("Value cannot be empty"))
                    }

                    form.copy(
                        keyField = form.keyField.copy(errors = keyErrors.toImmutableList()),
                        valueField = form.valueField.copy(errors = valueErrors.toImmutableList()),
                    )
                }

                val validatedLoadedForm = settingsForm?.map { form ->
                    val valueErrors = mutableListOf<StringValue>()
                    if (form.valueField.value.isBlank()) {
                        valueErrors.add(StringValue.Raw("Value cannot be empty"))
                    }

                    form.copy(valueField = form.valueField.copy(errors = valueErrors.toImmutableList()))
                }

                val isFormValid = validatedNewForm.keyField.isValid &&
                        validatedNewForm.valueField.isValid &&
                        validatedLoadedForm.orEmpty().all { it.valueField.isValid }

                _state.update { old ->
                    old.copy(
                        settingsForm = validatedLoadedForm?.toImmutableList(),
                        newSettingForm = validatedNewForm,
                        isFormValid = isFormValid,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
