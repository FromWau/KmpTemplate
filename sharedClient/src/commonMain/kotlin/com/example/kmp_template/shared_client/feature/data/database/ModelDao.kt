package com.example.kmp_template.shared_client.feature.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ModelEntity)

    @Query("SELECT * FROM models")
    suspend fun getAll(): List<ModelEntity>

    @Query("SELECT * FROM models")
    fun getAllFlow(): Flow<List<ModelEntity>>
}