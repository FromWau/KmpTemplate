package com.example.kmp_template.server.person.domain

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.server.person.domain.mapper.toDomain
import com.example.kmp_template.server.person.domain.mapper.toRpc
import com.example.kmp_template.server.person.domain.repository.PersonRepository
import com.example.kmp_template.shared_rpc.person.PeopleService
import com.example.kmp_template.shared_rpc.person.model.PersonRpc

class PeopleServiceImpl(
    private val repo: PersonRepository,
) : PeopleService {
    companion object {
        private const val TAG = "PeopleService"
    }

    override suspend fun savePerson(person: PersonRpc): Boolean {
        Log.tag(TAG).d { "savePerson called with person: $person" }
        return repo.savePerson(person.toDomain())
    }

    override suspend fun getPersonByName(name: String): PersonRpc? {
        Log.tag(TAG).d { "getPersonByName called with name: $name" }
        return repo.getPersonByName(name)?.toRpc()
    }

    override suspend fun getAllPeople(): List<PersonRpc> {
        Log.tag(TAG).d { "getAllPeople called" }
        return repo.getAllPeople().map { it.toRpc() }
    }

    override suspend fun deletePerson(person: PersonRpc): Boolean {
        Log.tag(TAG).d { "deletePerson called with person: $person" }
        return repo.deletePerson(person.toDomain())
    }

    override suspend fun deleteAllPeople(): Boolean {
        Log.tag(TAG).d { "deleteAllPeople called" }
        return repo.deleteAllPeople()
    }
}