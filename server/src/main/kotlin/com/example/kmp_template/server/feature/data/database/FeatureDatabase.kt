package com.example.kmp_template.server.feature.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.kmp_template.core.database.converter.LocaleDateTimeConverter
import com.example.kmp_template.core.database.converter.UuidConverter
import com.example.kmp_template.server.feature.data.database.dao.ModelDao
import com.example.kmp_template.server.feature.data.database.model.ModelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.io.files.Path
import java.io.File

@Database(
    entities = [ModelEntity::class],
    version = FeatureDatabase.DB_VERSION,
)
@TypeConverters(
    UuidConverter::class,
    LocaleDateTimeConverter::class,
)
abstract class FeatureDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelDao

    companion object {
        const val DB_NAME = "feature"
        const val DB_VERSION = 1
    }
}

private fun getFeatureDatabaseBuilder(dataDir: Path): RoomDatabase.Builder<FeatureDatabase> {
    val dbFile = File(
        dataDir.toString(),
        "${FeatureDatabase.DB_NAME}.db"
    )

    return Room.databaseBuilder(dbFile.absolutePath)
}

fun getFeatureDatabase(dataDir: Path): FeatureDatabase = getFeatureDatabaseBuilder(dataDir)
    .fallbackToDestructiveMigration(dropAllTables = true)
    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()