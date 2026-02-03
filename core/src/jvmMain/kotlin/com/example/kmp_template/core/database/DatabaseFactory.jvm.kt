package com.example.kmp_template.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.databaseFile
import kotlinx.io.files.SystemFileSystem

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseFactory(
    val dirs: SystemAppDirectories,
) {
    actual inline fun <reified T : RoomDatabase> create(dbname: String): RoomDatabase.Builder<T> {
        val dbFile = dirs.databaseFile(dbname)

        SystemFileSystem.createDirectories(
            dbFile.parent ?: error("Database file must have a parent directory")
        )

        return Room.databaseBuilder(name = dbFile.toString())
    }
}
