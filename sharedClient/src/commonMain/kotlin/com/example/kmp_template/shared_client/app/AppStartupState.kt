package com.example.kmp_template.shared_client.app

sealed interface AppStartupState {
    data object Initializing : AppStartupState
    data object Ready : AppStartupState
}