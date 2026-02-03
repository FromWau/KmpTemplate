package com.example.kmp_template.shared_client

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kmp_template.shared_client.app.App
import com.example.kmp_template.shared_client.di.initKoin

@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }
