package com.example.kmp_template.shared_rpc.person

import com.example.kmp_template.shared_rpc.person.model.PersonRpc
import kotlinx.rpc.annotations.Rpc

@Rpc
interface PeopleService {
    suspend fun savePerson(person: PersonRpc): Boolean

    suspend fun getPersonByName(name: String): PersonRpc?

    suspend fun getAllPeople(): List<PersonRpc>

    suspend fun deletePerson(person: PersonRpc): Boolean

    suspend fun deleteAllPeople(): Boolean
}
