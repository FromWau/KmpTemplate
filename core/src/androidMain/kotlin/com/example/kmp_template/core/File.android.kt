package com.example.kmp_template.core

import android.content.Context
import android.os.Environment
import kotlinx.io.files.Path

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class SystemAppDirectories(private val context: Context) {
    actual fun dataDir(): Path = Path(context.dataDir.absolutePath)

    actual fun configDir(): Path = Path(context.filesDir.absolutePath, "config")

    actual fun homeDir(): Path = Path(context.filesDir.absolutePath)

    actual fun mediaDir(): Path {
        @Suppress("DEPRECATION")
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        return Path(musicDir.absolutePath, "KmpTemplate")
    }
}