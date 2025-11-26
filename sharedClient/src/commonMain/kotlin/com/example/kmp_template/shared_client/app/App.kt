package com.example.kmp_template.shared_client.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.kmp_template.shared_client.core.navigation.NavigationHost
import com.example.kmp_template.shared_client.core.presentation.toast.ToastHost
import com.example.kmp_template.shared_client.home.presentation.HomeScreenRoot
import com.example.kmp_template.shared_client.settings.presentation.composable.SettingsScreenRoot
import com.example.kmp_template.shared_client.theme.AppTheme


@Composable
fun App() {
    AppTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                val navController = rememberNavController()

                NavigationHost(
                    navController = navController,
                    startDestination = Route.Graph,
                ) {
                    navigation<Route.Graph>(startDestination = Route.Home) {
                        composable<Route.Home> {
                            HomeScreenRoot()
                        }

                        composable<Route.Settings> {
                            SettingsScreenRoot()
                        }
                    }
                }

                ToastHost()
            }
        }
    }
}
