package com.example.kmp_template.shared_client.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.kmp_template.shared_client.core.LoadingCircle
import com.example.kmp_template.shared_client.core.navigation.NavigationHost
import com.example.kmp_template.shared_client.core.permission.PermissionHost
import com.example.kmp_template.shared_client.core.toast.presentation.ToastHost
import com.example.kmp_template.shared_client.home.presentation.HomeScreen
import com.example.kmp_template.shared_client.setting.presentation.composable.SettingScreen
import com.example.kmp_template.shared_client.theme.AppTheme
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun App(
    viewModel: AppStartupViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    App(
        state = state,
        initApp = { viewModel.initializeApp() },
    )
}

@Composable
private fun App(
    state: AppStartupState,
    initApp: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(1.seconds)
        initApp()
    }

    AppTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    is AppStartupState.Ready -> AppReady()
                    is AppStartupState.Initializing -> AppInitializing()
                }

                ToastHost(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
                PermissionHost()
            }
        }
    }
}

@Composable
private fun AppReady() {
    NavigationHost(
        navController = rememberNavController(),
        startDestination = Route.Graph,
    ) {
        navigation<Route.Graph>(startDestination = Route.Home) {
            composable<Route.Home> {
                HomeScreen()
            }

            composable<Route.Setting> {
                SettingScreen()
            }
        }
    }
}

@Composable
private fun AppInitializing() {
    LoadingCircle(
        modifier = Modifier.size(48.dp)
    )
}