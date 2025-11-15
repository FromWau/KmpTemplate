package com.example.kmp_template.compose_app

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kmp_template.shared_client.app.App
import com.example.kmp_template.shared_client.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }