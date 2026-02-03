package com.example.kmp_template.desktop_app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.kmp_template.shared_client.app.App
import com.example.kmp_template.shared_client.di.initKoin

fun main() {
    initKoin()

    // INFO: Skiko is slow on default settings, especially on Linux. To improve performance, we can change the rendering API.
    //  Choose one: SOFTWARE, SOFTWARE_FAST, OPENGL, METAL (Mac), VULKAN (Linux/Windows)
    System.setProperty("skiko.renderApi", "SOFTWARE_FAST")
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KmpTemplate",
        ) {
            App()
        }
    }
}
