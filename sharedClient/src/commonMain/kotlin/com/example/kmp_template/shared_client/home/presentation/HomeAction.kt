package com.example.kmp_template.shared_client.home.presentation

import androidx.compose.runtime.Immutable

@Immutable
sealed interface HomeAction {
    data object OnSettingsClicked : HomeAction
}