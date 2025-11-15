package com.example.kmp_template.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kmp_template.core.SystemAppDirectories

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseFactory(
    val dirs: SystemAppDirectories,
) {
    actual inline fun <reified T : RoomDatabase> create(dbname: String): RoomDatabase.Builder<T> {
        val dbFile = dirs.databaseFile(dbname)

        return Room.databaseBuilder(
            name = dbFile.toString(),
        )
    }
}
