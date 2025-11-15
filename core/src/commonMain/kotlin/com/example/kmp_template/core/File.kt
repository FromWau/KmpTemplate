package com.example.kmp_template.core

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "Unused")
expect class SystemAppDirectories {
    fun dataDir(): Path
    fun databaseFile(dbname: String): Path
}

fun SystemAppDirectories.mediaDir(): Path = Path(dataDir(), "media")

val Path.fileExtension: String?
    get() {
        if (SystemFileSystem.metadataOrNull(this)?.isRegularFile != true) return null

        val extension = this.name.substringAfter(".")
        return if (extension == this.name) null else extension
    }