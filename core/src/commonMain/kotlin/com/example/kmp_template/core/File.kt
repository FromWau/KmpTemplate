package com.example.kmp_template.core

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "Unused")
expect class SystemAppDirectories {
    fun dataDir(): Path
    fun configDir(): Path
    fun homeDir(): Path
    fun mediaDir(): Path
}

fun SystemAppDirectories.databaseFile(dbname: String): Path = Path(dataDir(), "databases", dbname)
fun SystemAppDirectories.logDir(): Path = Path(dataDir(), "logs")

val Path.fileExtension: String?
    get() {
        if (SystemFileSystem.metadataOrNull(this)?.isRegularFile != true) return null

        val extension = this.name.substringAfter(".")
        return if (extension == this.name) null else extension
    }

val Path.nameWithoutExtension: String
    get() {
        val extension = this.fileExtension ?: return this.name
        return this.name.removeSuffix(".$extension")
    }

fun Path.walkTopDown(): Sequence<Path> = sequence {
    val metadata = SystemFileSystem.metadataOrNull(this@walkTopDown)
    if (metadata == null || !metadata.isDirectory) {
        yield(this@walkTopDown)
        return@sequence
    }

    val stack = ArrayDeque<Path>()
    stack.add(this@walkTopDown)

    while (stack.isNotEmpty()) {
        val current = stack.removeFirst()

        for (child in SystemFileSystem.list(current)) {
            val childMetadata = SystemFileSystem.metadataOrNull(child)
            when {
                childMetadata == null -> continue
                childMetadata.isDirectory -> stack.addFirst(child)
                childMetadata.isRegularFile -> yield(child)
            }
        }
    }
}

fun Path.resolveTilde(
    systemAppDirectories: SystemAppDirectories,
): Path {
    val homeDir = systemAppDirectories.homeDir().toString()
    val pathStr = this.toString()
    return if (pathStr.startsWith("~")) {
        Path(homeDir + pathStr.removePrefix("~"))
    } else {
        this
    }
}
