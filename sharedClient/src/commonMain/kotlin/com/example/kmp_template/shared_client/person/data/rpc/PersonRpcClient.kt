package com.example.kmp_template.shared_client.person.data.rpc

import com.example.kmp_template.shared_client.config.AppConfigProvider
import com.example.kmp_template.shared_client.config.ServerConnectionConfig
import com.example.kmp_template.shared_rpc.person.PeopleService
import com.example.kmp_template.shared_rpc.person.model.PersonRpc
import io.ktor.client.HttpClient
import io.ktor.http.encodedPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

class PersonRpcClient(
    private val ktorClient: HttpClient,
    private val appConfigProvider: AppConfigProvider,
) {
    private val mutex = Mutex()
    private var currentServerConfig: ServerConnectionConfig? = null
    private var rpcClientScope: CoroutineScope? = null
    private var rpcClient: RpcClient? = null
    private var peopleService: PeopleService? = null

    private suspend fun service(): PeopleService = mutex.withLock {
        val serverConfig = appConfigProvider.config.value.server
        if (serverConfig != currentServerConfig) {
            rpcClientScope?.cancel()
            rpcClientScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

            rpcClient = ktorClient.rpc {
                url {
                    host = serverConfig.host
                    port = serverConfig.port
                    encodedPath = "/person"
                }
                rpcConfig {
                    serialization {
                        json()
                    }
                }
            }
            peopleService = rpcClient!!.withService()
            currentServerConfig = serverConfig
        }

        return@withLock peopleService!!
    }

    suspend fun savePerson(person: PersonRpc): Boolean {
        return service().savePerson(person)
    }

    suspend fun getPersonByName(name: String): PersonRpc? {
        return service().getPersonByName(name)
    }

    suspend fun getAllPeople(): List<PersonRpc> {
        return service().getAllPeople()
    }

    suspend fun deletePerson(person: PersonRpc): Boolean {
        return service().deletePerson(person)
    }

    suspend fun deleteAllPeople(): Boolean {
        return service().deleteAllPeople()
    }
}