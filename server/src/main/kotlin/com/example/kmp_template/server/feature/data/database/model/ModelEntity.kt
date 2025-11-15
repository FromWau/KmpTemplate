package com.example.kmp_template.server.feature.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "models",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class ModelEntity(
    @PrimaryKey
    @ColumnInfo("id") val id: Uuid,
    @ColumnInfo("name") val name: String,
)