package com.example.kmp_template.shared_client.person.domain.repository

import com.example.kmp_template.core.result.EmptyResult
import com.example.kmp_template.core.result.Error
import com.example.kmp_template.core.result.Result
import com.example.kmp_template.shared_client.person.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    suspend fun savePerson(person: Person): EmptyResult<PersonRepoError.SaveError>

    fun getAllLocalPeople(): Result<Flow<List<Person>>, PersonRepoError.GetError>

    suspend fun getAllRemotePeople(): Result<List<Person>, PersonRepoError.GetError>

    suspend fun getPersonByNameAndLocal(
        name: String,
        local: Boolean,
    ): Result<Person, PersonRepoError.GetError>

    suspend fun deletePerson(person: Person): EmptyResult<PersonRepoError.GetError>

    suspend fun deleteAllPeople(): EmptyResult<PersonRepoError.GetError>
}

sealed interface PersonRepoError {
    sealed interface SaveError : PersonRepoError, Error {
        data class GenericError(val throwable: Throwable) : SaveError
    }

    sealed interface GetError : PersonRepoError, Error {
        data class NotFound(val name: String) : GetError
        data class GenericError(val throwable: Throwable) : GetError
    }
}