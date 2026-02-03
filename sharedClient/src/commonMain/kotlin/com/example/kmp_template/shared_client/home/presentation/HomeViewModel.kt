package com.example.kmp_template.shared_client.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.core.result.onError
import com.example.kmp_template.core.result.onSuccess
import com.example.kmp_template.shared_client.app.Route
import com.example.kmp_template.shared_client.config.AppConfigProvider
import com.example.kmp_template.shared_client.core.feature.domain.model.Model
import com.example.kmp_template.shared_client.core.feature.domain.repository.FeatureRepository
import com.example.kmp_template.shared_client.core.navigation.NavigationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class HomeViewModel(
    private val nav: NavigationService,
    private val featureRepo: FeatureRepository,
    private val appConfigProvider: AppConfigProvider,
) : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            delay(2.seconds)
            val model1 = Model(id = Uuid.random(), name = "TestModel1")
            testRpc(model1)

            delay(2.seconds)
            appConfigProvider.updateConfig {
                it.copy(server = it.server.copy(port = 8081))
            }

            delay(2.seconds)
            val model2 = Model(id = Uuid.random(), name = "TestModel2")
            testRpc(model2)
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnSettingClicked -> {
                nav.to(Route.Setting)
            }
        }
    }

    // Example rpc call
    private suspend fun testRpc(model: Model) {
        Log.tag(TAG).w { "Starting RPC test" }

        val saveResult = featureRepo.saveModel(model)
        saveResult
            .onSuccess { Log.tag(TAG).d { "Model saved: $model" } }
            .onError {
                Log.tag(TAG).e { "Failed to save model: $model" }
                error("RPC test failed. template setup broken")
            }

        val foundResult = featureRepo.getModelByName(model.name)
        foundResult
            .onSuccess { Log.tag(TAG).d { "Model loaded: ${it.name}" } }
            .onError {
                Log.tag(TAG).e { "Failed to load model with name: ${model.name}" }
                error("RPC test failed. template setup broken")
            }

        Log.tag(TAG).w { "RPC test completed" }
    }
}
