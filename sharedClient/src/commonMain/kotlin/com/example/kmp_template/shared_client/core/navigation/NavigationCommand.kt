package com.example.kmp_template.shared_client.core.navigation

import com.example.kmp_template.shared_client.app.Route

sealed interface NavigationCommand {
    /**
     * Navigate back in the navigation stack
     */
    data object Back : NavigationCommand

    /**
     * Navigate to a specific route
     */
    data class To(val route: Route) : NavigationCommand

    /**
     * Navigate to a route and clear the back stack up to a destination
     */
    data class ToAndClearUpTo(
        val route: Route,
        val clearUpTo: Route,
        val inclusive: Boolean = false
    ) : NavigationCommand

    /**
     * Navigate to a route and clear the entire back stack
     */
    data class ToAndClearAll(val route: Route) : NavigationCommand
}
