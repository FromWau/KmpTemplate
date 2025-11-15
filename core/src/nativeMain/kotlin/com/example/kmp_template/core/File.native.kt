package com.example.kmp_template.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class SystemAppDirectories {
    @OptIn(ExperimentalForeignApi::class)
    actual fun dataDir(): Path {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )

        return Path(requireNotNull(documentDirectory?.path))
    }

    actual fun databaseFile(dbname: String): Path =
        Path(dataDir(), "databases", dbname)
}
