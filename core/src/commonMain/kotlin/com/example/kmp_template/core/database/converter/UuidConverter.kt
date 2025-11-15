package com.example.kmp_template.core.database.converter

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

class UuidConverter {
    @TypeConverter
    fun fromUuid(uuid: Uuid): String = uuid.toString()

    @TypeConverter
    fun toUuid(uuidString: String): Uuid = Uuid.parse(uuidString)
}