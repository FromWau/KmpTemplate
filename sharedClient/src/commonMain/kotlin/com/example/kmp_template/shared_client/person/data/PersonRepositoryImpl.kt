package com.example.kmp_template.shared_client.person.data

import com.example.kmp_template.core.result.EmptyResult
import com.example.kmp_template.core.result.Result
import com.example.kmp_template.shared_client.person.data.database.PersonDao
import com.example.kmp_template.shared_client.person.data.mapper.toDomain
import com.example.kmp_template.shared_client.person.data.mapper.toEntity
import com.example.kmp_template.shared_client.person.data.mapper.toRpc
import com.example.kmp_template.shared_client.person.data.rpc.PersonRpcClient
import com.example.kmp_template.shared_client.person.domain.model.Person
import com.example.kmp_template.shared_client.person.domain.repository.PersonRepoError
import com.example.kmp_template.shared_client.person.domain.repository.PersonRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonRepositoryImpl(
    private val personRpcClient: PersonRpcClient,
    private val personDao: PersonDao,
) : PersonRepository {
    override suspend fun savePerson(person: Person): EmptyResult<PersonRepoError.SaveError> {
        try {
            if (person.local) {
                personDao.upsert(person.toEntity())
                return Result.Success(Unit)
            } else {
                personRpcClient.savePerson(person.toRpc())
            }

            return Result.Success(Unit)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(PersonRepoError.SaveError.GenericError(e))
        }
    }

    override fun getAllLocalPeople(): Result<Flow<List<Person>>, PersonRepoError.GetError> {
        try {
            val foundPeople = personDao.getAllFlow()
                .map { peopleEntities -> peopleEntities.map { it.toDomain() } }
            return Result.Success(foundPeople)
        } catch (e: Exception) {
            return Result.Error(PersonRepoError.GetError.GenericError(e))
        }
    }

    override suspend fun getAllRemotePeople(): Result<List<Person>, PersonRepoError.GetError> {
        try {
            val foundPeople = personRpcClient.getAllPeople().map { it.toDomain() }
            return Result.Success(foundPeople)
        } catch (
            e: Exception,
        ) {
            currentCoroutineContext().ensureActive()
            return Result.Error(PersonRepoError.GetError.GenericError(e))
        }
    }

    override suspend fun getPersonByNameAndLocal(
        name: String,
        local: Boolean,
    ): Result<Person, PersonRepoError.GetError> {
        return if (local) {
            getPersonByNameFromLocal(name)
        } else {
            getPersonByNameFromRemote(name)
        }
    }

    private suspend fun getPersonByNameFromLocal(name: String): Result<Person, PersonRepoError.GetError> {
        try {
            val foundPerson = personDao.getPersonByName(name)?.toDomain()
            return if (foundPerson != null) {
                Result.Success(foundPerson)
            } else {
                Result.Error(PersonRepoError.GetError.NotFound(name))
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(PersonRepoError.GetError.GenericError(e))
        }
    }

    private suspend fun getPersonByNameFromRemote(name: String): Result<Person, PersonRepoError.GetError> {
        try {
            val foundPerson = personRpcClient.getPersonByName(name)?.toDomain()
            return if (foundPerson != null) {
                Result.Success(foundPerson)
            } else {
                Result.Error(PersonRepoError.GetError.NotFound(name))
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(PersonRepoError.GetError.GenericError(e))
        }
    }

    override suspend fun deletePerson(person: Person): EmptyResult<PersonRepoError.GetError> {
        return if (person.local) {
            deletePersonFromLocal(person)
        } else {
            deletePersonFromRemote(person)
        }
    }

    private suspend fun deletePersonFromLocal(person: Person): EmptyResult<PersonRepoError.GetError> {
        return try {
            personDao.delete(person.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Result.Error(PersonRepoError.GetError.GenericError(e))
        }
    }

    private suspend fun deletePersonFromRemote(person: Person): EmptyResult<PersonRepoError.GetError> {
        return try {
            // Assuming the RPC client has a deletePerson method
            val deleted = personRpcClient.deletePerson(person.toRpc())
            if (deleted) {
                Result.Success(Unit)
            } else {
                Result.Error(PersonRepoError.GetError.GenericError(Exception("Failed to delete person via RPC")))
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(PersonRepoError.GetError.GenericError(e))
        }
    }

    override suspend fun deleteAllPeople(): EmptyResult<PersonRepoError.GetError> {
        return try {
            personDao.deleteAll()
            personRpcClient.deleteAllPeople()
            Result.Success(Unit)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Result.Error(PersonRepoError.GetError.GenericError(e))
        }
    }
}