package com.example.kmp_template.compose_app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.kmp_template.shared_client.app.App
import com.example.kmp_template.shared_client.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KmpTemplate",
        ) {
            App()
        }
    }
}