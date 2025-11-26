package com.example.kmp_template.shared_client.home.presentation

sealed interface HomeAction {
    data object OnSettingsClicked : HomeAction
}