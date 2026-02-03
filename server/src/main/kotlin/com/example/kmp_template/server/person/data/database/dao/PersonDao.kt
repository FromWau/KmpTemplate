package com.example.kmp_template.server.person.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kmp_template.server.person.data.database.model.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PersonEntity): Long

    @Delete
    suspend fun delete(entity: PersonEntity): Int

    @Query("SELECT * FROM people")
    suspend fun getAllPeople(): List<PersonEntity>

    @Query("SELECT * FROM people")
    fun getAllPeopleFlow(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE name = :name")
    suspend fun findByName(name: String): PersonEntity?

    @Query("DELETE FROM people")
    suspend fun deleteAll()
}