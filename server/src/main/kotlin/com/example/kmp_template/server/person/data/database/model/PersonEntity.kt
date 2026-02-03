package com.example.kmp_template.server.person.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey
    @ColumnInfo("id") val id: Uuid,
    @ColumnInfo("name") val name: String,
)