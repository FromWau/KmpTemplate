package com.example.kmp_template.core

import kotlinx.io.files.Path
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class SystemAppDirectories {
    actual fun dataDir(): Path {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDirName = "kmp_template"

        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), appDataDirName)
            os.contains("mac") -> File(userHome, "Library/Application Support/$appDataDirName")
            else -> File("$userHome/.local/share/$appDataDirName")
        }

        return Path(appDataDir.absolutePath)
    }

    actual fun databaseFile(dbname: String): Path =
        Path(dataDir(), "databases", dbname)
}
