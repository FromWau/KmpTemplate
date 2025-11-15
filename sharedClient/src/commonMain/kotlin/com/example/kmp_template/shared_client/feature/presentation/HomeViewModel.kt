package com.example.kmp_template.shared_client.feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp_template.shared_client.feature.domain.model.Model
import com.example.kmp_template.shared_client.feature.domain.repository.FeatureRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class HomeViewModel(
    private val featureRepo: FeatureRepository,
) : ViewModel() {
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()
        .onStart { observeAllModels() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnBack -> {}
        }
    }

    private fun observeAllModels() {
        viewModelScope.launch {
            repeat(10) {
                featureRepo.insert(
                    Model(id = Uuid.random(), name = "Test")
                )

                delay(1.seconds)
            }
        }

        featureRepo.getAllModelsFlow()
            .onEach { models ->
                _state.update {
                    it.copy(models = models)
                }
            }
            .launchIn(viewModelScope)
    }
}