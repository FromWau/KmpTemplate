package com.example.kmp_template.shared_client.feature.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.kmp_template.core.database.converter.UuidConverter

@Database(
    entities = [ModelEntity::class],
    version = FeatureDatabase.DB_VERSION,
)
@TypeConverters(UuidConverter::class)
abstract class FeatureDatabase : RoomDatabase() {
    abstract val modelDao: ModelDao

    companion object {
        const val DB_NAME = "feature.db"
        const val DB_VERSION = 1
    }
}

@Suppress(
    "NO_ACTUAL_FOR_EXPECT", // Room creates the actual implementation
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"
)
expect object FeatureDatabaseConstructor : RoomDatabaseConstructor<FeatureDatabase> {
    override fun initialize(): FeatureDatabase
}