package com.example.kmp_template.core.database

import androidx.room.RoomDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseFactory {
    inline fun <reified T : RoomDatabase> create(dbname: String): RoomDatabase.Builder<T>
}