package com.example.kmp_template.core

import android.content.Context
import kotlinx.io.files.Path

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class SystemAppDirectories(private val context: Context) {
    actual fun dataDir(): Path = Path(context.dataDir.absolutePath)
    actual fun databaseFile(dbname: String): Path =
        Path(context.getDatabasePath(dbname).absolutePath)
}