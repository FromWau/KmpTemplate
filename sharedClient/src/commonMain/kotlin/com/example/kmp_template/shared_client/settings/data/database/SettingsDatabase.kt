package com.example.kmp_template.shared_client.settings.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [SettingsEntity::class],
    version = SettingsDatabase.DB_VERSION,
)
@ConstructedBy(SettingsDatabaseConstructor::class)
abstract class SettingsDatabase : RoomDatabase() {
    abstract val settingsDao: SettingsDao

    companion object {
        const val DB_NAME = "settings.db"
        const val DB_VERSION = 1
    }
}

@Suppress(
    "KotlinNoActualForExpect",
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
)
expect object SettingsDatabaseConstructor : RoomDatabaseConstructor<SettingsDatabase> {
    override fun initialize(): SettingsDatabase
}
