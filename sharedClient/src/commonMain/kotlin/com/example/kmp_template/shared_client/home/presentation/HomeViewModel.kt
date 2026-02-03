package com.example.kmp_template.shared_client.home.presentation

import androidx.lifecycle.ViewModel
import com.example.kmp_template.shared_client.app.Route
import com.example.kmp_template.shared_client.core.navigation.NavigationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val nav: NavigationService,
) : ViewModel() {
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnSettingClicked -> {
                nav.to(Route.Setting)
            }
        }
    }
}