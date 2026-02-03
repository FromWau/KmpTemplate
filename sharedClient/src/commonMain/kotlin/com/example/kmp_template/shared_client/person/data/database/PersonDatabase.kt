package com.example.kmp_template.shared_client.person.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kmp_template.core.database.converter.LocaleDateTimeConverter
import com.example.kmp_template.core.database.converter.UuidConverter

@Database(
    entities = [PersonEntity::class],
    version = PersonDatabase.DB_VERSION,
)
@TypeConverters(
    UuidConverter::class,
    LocaleDateTimeConverter::class,
)
abstract class PersonDatabase : RoomDatabase() {
    abstract val personDao: PersonDao

    companion object {
        const val DB_NAME = "client_person"
        const val DB_VERSION = 1
    }
}
