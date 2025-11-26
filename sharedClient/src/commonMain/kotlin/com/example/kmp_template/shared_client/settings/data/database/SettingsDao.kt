package com.example.kmp_template.shared_client.settings.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg entity: SettingsEntity)

    @Query("SELECT * FROM settings")
    suspend fun getAll(): List<SettingsEntity>

    @Query("SELECT * FROM settings")
    fun getAllFlow(): Flow<List<SettingsEntity>>

    @Delete
    suspend fun delete(entity: SettingsEntity): Int
}
