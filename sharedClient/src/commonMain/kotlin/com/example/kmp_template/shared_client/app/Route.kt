package com.example.kmp_template.shared_client.app

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
sealed interface Route {
    @Serializable
    data object Graph : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Person : Route
}