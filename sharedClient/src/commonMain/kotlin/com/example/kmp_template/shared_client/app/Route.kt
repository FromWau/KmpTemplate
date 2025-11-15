package com.example.kmp_template.shared_client.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Graph : Route

    @Serializable
    data object Home : Route
}