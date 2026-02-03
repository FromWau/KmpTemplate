package com.example.kmp_template.server.person.data

import com.example.kmp_template.server.person.data.database.dao.PersonDao
import com.example.kmp_template.server.person.data.database.mapper.toDomain
import com.example.kmp_template.server.person.data.database.mapper.toEntity
import com.example.kmp_template.server.person.domain.model.Person
import com.example.kmp_template.server.person.domain.repository.PersonRepository

class PersonRepositoryImpl(
    private val personDao: PersonDao,
) : PersonRepository {
    override suspend fun getPersonByName(name: String): Person? {
        return personDao.findByName(name)?.toDomain()
    }

    override suspend fun savePerson(person: Person): Boolean {
        personDao.upsert(person.toEntity())
        return true
    }

    override suspend fun getAllPeople(): List<Person> {
        return personDao.getAllPeople().map { it.toDomain() }
    }

    override suspend fun deletePerson(person: Person): Boolean {
        return personDao.delete(person.toEntity()) == 1
    }

    override suspend fun deleteAllPeople(): Boolean {
        personDao.deleteAll()
        return true
    }
}