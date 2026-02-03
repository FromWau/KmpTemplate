package com.example.kmp_template.shared_client.setting.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [SettingEntity::class],
    version = SettingDatabase.DB_VERSION,
)
@ConstructedBy(SettingsDatabaseConstructor::class)
abstract class SettingDatabase : RoomDatabase() {
    abstract val settingDao: SettingDao

    companion object {
        const val DB_NAME = "settings.db"
        const val DB_VERSION = 1
    }
}

@Suppress(
    "KotlinNoActualForExpect",
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
)
expect object SettingsDatabaseConstructor : RoomDatabaseConstructor<SettingDatabase> {
    override fun initialize(): SettingDatabase
}
