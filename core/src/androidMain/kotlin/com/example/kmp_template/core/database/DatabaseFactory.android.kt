package com.example.kmp_template.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kmp_template.core.SystemAppDirectories

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseFactory(
    val dirs: SystemAppDirectories,
    val context: Context,
) {
    actual inline fun <reified T : RoomDatabase> create(
        dbname: String,
    ): RoomDatabase.Builder<T> {
        val dbFile = dirs.databaseFile(dbname)

        return Room.databaseBuilder(
            context = context.applicationContext,
            name = dbFile.toString(),
        )
    }
}
