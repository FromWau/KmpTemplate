package com.example.kmp_template.shared_client.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.example.kmp_template.shared_client.app.Route
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun <T : Route> NavigationHost(
    navController: NavHostController,
    startDestination: T,
    modifier: Modifier = Modifier,
    navigationService: NavigationService = koinInject(),
    builder: NavGraphBuilder.() -> Unit,
) {
    LaunchedEffect(navigationService) {
        navigationService.commands.collectLatest { command ->
            when (command) {
                is NavigationCommand.Back -> {
                    navController.navigateUp()
                }

                is NavigationCommand.To -> {
                    navController.navigate(command.route)
                }

                is NavigationCommand.ToAndClearUpTo -> {
                    navController.navigate(
                        route = command.route,
                        navOptions = navOptions {
                            popUpTo(command.clearUpTo) {
                                inclusive = command.inclusive
                            }
                        }
                    )
                }

                is NavigationCommand.ToAndClearAll -> {
                    navController.navigate(
                        route = command.route,
                        navOptions = navOptions {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    )
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        builder = builder
    )
}
