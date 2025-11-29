@file:Suppress("unused")

package com.example.kmp_template.shared_client.core.presentation.navigation

import com.example.kmp_template.shared_client.app.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class NavigationService {
    private val _commands = Channel<NavigationCommand>(Channel.BUFFERED)
    val commands: Flow<NavigationCommand> = _commands.receiveAsFlow()

    /**
     * Navigate to a route
     */
    fun to(route: Route) {
        _commands.trySend(NavigationCommand.To(route))
    }

    /**
     * Navigate back
     */
    fun back() {
        _commands.trySend(NavigationCommand.Back)
    }

    /**
     * Navigate to a route and clear back stack up to a destination
     */
    fun toAndClearUpTo(route: Route, clearUpTo: Route, inclusive: Boolean = false) {
        _commands.trySend(NavigationCommand.ToAndClearUpTo(route, clearUpTo, inclusive))
    }

    /**
     * Navigate to a route and clear entire back stack
     */
    fun toAndClearAll(route: Route) {
        _commands.trySend(NavigationCommand.ToAndClearAll(route))
    }

    /**
     * Send a custom navigation command
     */
    fun send(command: NavigationCommand) {
        _commands.trySend(command)
    }
}
