package com.example.kmp_template.shared_client.core.feature.data.rpc

import com.example.kmp_template.shared_client.config.AppConfigProvider
import com.example.kmp_template.shared_client.config.ServerConnectionConfig
import com.example.kmp_template.shared_rpc.feature.FeatureService
import com.example.kmp_template.shared_rpc.feature.model.ModelRpc
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

class FeatureRpcClient(
    private val ktorClient: HttpClient,
    private val appConfigProvider: AppConfigProvider,
) {
    private val mutex = Mutex()
    private var currentServerConfig: ServerConnectionConfig? = null
    private var rpcClientScope: CoroutineScope? = null
    private var rpcClient: RpcClient? = null
    private var featureService: FeatureService? = null

    private suspend fun service(): FeatureService = mutex.withLock {
        val serverConfig = appConfigProvider.config.value.server
        if (serverConfig != currentServerConfig) {
            rpcClientScope?.cancel()
            rpcClientScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

            rpcClient = ktorClient.rpc {
                url {
                    host = serverConfig.host
                    port = serverConfig.port
                    encodedPath = "/feature"
                }
                rpcConfig {
                    serialization {
                        json()
                    }
                }
            }
            featureService = rpcClient!!.withService()
            currentServerConfig = serverConfig
        }

        return@withLock featureService!!
    }

    suspend fun saveModel(model: ModelRpc): Boolean {
        return service().saveModel(model)
    }

    suspend fun getModelByName(name: String): ModelRpc? {
        return service().getModelByName(name)
    }
}