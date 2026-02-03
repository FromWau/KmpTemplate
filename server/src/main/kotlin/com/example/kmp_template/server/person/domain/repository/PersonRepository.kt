package com.example.kmp_template.server.person.domain.repository

import com.example.kmp_template.server.person.domain.model.Person

interface PersonRepository {
    suspend fun savePerson(person: Person): Boolean

    suspend fun getPersonByName(name: String): Person?

    suspend fun getAllPeople(): List<Person>

    suspend fun deletePerson(person: Person): Boolean

    suspend fun deleteAllPeople(): Boolean
}
