package com.example.kmp_template.shared_client.feature.presentation

sealed interface HomeAction {
    data object OnBack : HomeAction
}