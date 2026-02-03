package com.example.kmp_template.server.person.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kmp_template.core.database.converter.LocaleDateTimeConverter
import com.example.kmp_template.core.database.converter.UuidConverter
import com.example.kmp_template.server.person.data.database.dao.PersonDao
import com.example.kmp_template.server.person.data.database.model.PersonEntity

@Database(
    entities = [PersonEntity::class],
    version = PersonDatabase.DB_VERSION,
)
@TypeConverters(
    UuidConverter::class,
    LocaleDateTimeConverter::class,
)
abstract class PersonDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        const val DB_NAME = "server_person"
        const val DB_VERSION = 1
    }
}
