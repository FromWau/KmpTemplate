package com.example.kmp_template.shared_client.setting.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg entity: SettingEntity)

    @Query("SELECT * FROM settings")
    suspend fun getAll(): List<SettingEntity>

    @Query("SELECT * FROM settings")
    fun getAllFlow(): Flow<List<SettingEntity>>

    @Delete
    suspend fun delete(entity: SettingEntity): Int
}
