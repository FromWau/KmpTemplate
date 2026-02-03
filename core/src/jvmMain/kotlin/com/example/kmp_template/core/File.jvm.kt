package com.example.kmp_template.core

import kotlinx.io.files.Path
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class SystemAppDirectories {
    private val os = System.getProperty("os.name").lowercase()
    private val userHome = System.getProperty("user.home")
    private val appName = "kmp_template"

    actual fun dataDir(): Path {
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), appName)
            os.contains("mac") -> File(userHome, "Library/Application Support/$appName")
            else -> {
                System.getenv("XDG_DATA_HOME")
                    ?.let { File(it, appName) }
                    ?: File("$userHome/.local/share/$appName")
            }
        }

        return Path(appDataDir.absolutePath)
    }

    actual fun configDir(): Path {
        val configDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), appName)
            os.contains("mac") -> File(userHome, "Library/Application Support/$appName")
            else -> {
                System.getenv("XDG_CONFIG_HOME")
                    ?.let { File(it, appName) }
                    ?: File("$userHome/.config/$appName")
            }
        }

        return Path(configDir.absolutePath)
    }

    actual fun homeDir(): Path = Path(userHome)

    actual fun mediaDir(): Path = Path(dataDir(), "media")
}
