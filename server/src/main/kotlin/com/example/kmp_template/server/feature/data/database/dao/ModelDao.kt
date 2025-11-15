package com.example.kmp_template.server.feature.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kmp_template.server.feature.data.database.model.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(model: ModelEntity)

    @Update
    suspend fun update(model: ModelEntity)

    @Delete
    suspend fun delete(model: ModelEntity)

    @Query("SELECT * FROM models")
    suspend fun getAllModels(): List<ModelEntity>

    @Query("SELECT * FROM models")
    fun getAllModelsFlow(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE name = :name")
    suspend fun findByName(name: String): ModelEntity?
}