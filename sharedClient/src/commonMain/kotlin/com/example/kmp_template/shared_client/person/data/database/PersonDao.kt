package com.example.kmp_template.shared_client.person.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg entity: PersonEntity)

    @Query("SELECT * FROM people")
    suspend fun getAll(): List<PersonEntity>

    @Query("SELECT * FROM people")
    fun getAllFlow(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE name = :name")
    suspend fun getPersonByName(name: String): PersonEntity?

    @Delete
    suspend fun delete(entity: PersonEntity): Int

    @Query("DELETE FROM people")
    suspend fun deleteAll(): Int
}
