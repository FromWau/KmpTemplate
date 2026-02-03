package com.example.kmp_template.shared_client.setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_client.core.StringValue
import com.example.kmp_template.shared_client.core.navigation.NavigationService
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.setting.domain.model.Setting
import com.example.kmp_template.shared_client.setting.domain.repository.SettingRepository
import com.example.kmp_template.shared_client.setting.presentation.mapper.toSetting
import com.example.kmp_template.shared_client.setting.presentation.mapper.toSettingForm
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

class SettingViewModel(
    private val settingRepository: SettingRepository,
    private val nav: NavigationService,
    private val toastService: ToastService,
) : ViewModel() {
    companion object {
        const val TAG = "SettingViewModel"
    }

    private val _state = MutableStateFlow(SettingState())

    @OptIn(FlowPreview::class)
    val state = _state.asStateFlow()
        .onStart {
            observeSetting()
            observeFormForValidation()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds),
            _state.value
        )

    fun onAction(action: SettingAction) {
        Log.tag(TAG).v { "action received $action" }

        when (action) {
            SettingAction.OnBackClicked -> nav.back()

            SettingAction.OnSaveSetting -> {
                val currentState = _state.value

                if (!currentState.isFormValid) {
                    Log.tag(TAG).v { "form is not valid, not saving" }
                    return
                }

                val currentSettings = currentState.settingForm
                    .orEmpty()
                    .map { form -> form.toSetting() }

                val newSetting = currentState.newSettingForm.toSetting()

                val allSettings = (currentSettings + newSetting).filterNotNull()

                viewModelScope.launch {
                    val result = settingRepository.upsertSetting(Setting(allSettings))

                    if (result) {
                        toastService.showSuccess(StringValue.Raw("Setting saved successfully"))
                        _state.update { old ->
                            old.copy(newSettingForm = SettingState.NewForm())
                        }
                    } else {
                        toastService.showError(StringValue.Raw("Failed to save settings. Please try again."))
                    }
                }
            }

            is SettingAction.LoadedSetting.OnDeleteSetting -> {
                viewModelScope.launch {
                    val result = settingRepository.deleteSetting(action.form.toSetting())

                    if (result) {
                        toastService.showSuccess(StringValue.Raw("Setting deleted successfully"))
                    } else {
                        toastService.showError(StringValue.Raw("Failed to delete setting. Please try again."))
                    }
                }
            }

            is SettingAction.LoadedSetting.OnValueChanged -> {
                val currentState = _state.value

                val updatedSettings = currentState.settingForm
                    ?.map {
                        if (it.key == action.form.key) {
                            it.copy(valueField = action.form.valueField)
                        } else {
                            it
                        }
                    }

                _state.update { old ->
                    old.copy(settingForm = updatedSettings?.toImmutableList())
                }
            }

            is SettingAction.NewSetting.OnKeyChanged -> {
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

            is SettingAction.NewSetting.OnValueChanged -> {
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

    private fun observeSetting() {
        settingRepository.getSettingFlow()
            .onEach { setting ->
                Log.tag(TAG).v { "setting changed" }
                val settingForm = setting.toSettingForm()

                _state.update { old ->
                    old.copy(settingForm = settingForm)
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun observeFormForValidation() {
        state
            .map { it.settingForm to it.newSettingForm }
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
                        settingForm = validatedLoadedForm?.toImmutableList(),
                        newSettingForm = validatedNewForm,
                        isFormValid = isFormValid,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
