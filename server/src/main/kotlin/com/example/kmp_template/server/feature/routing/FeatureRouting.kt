package com.example.kmp_template.server.feature.routing

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_rpc.feature.FeatureService
import io.ktor.server.routing.Route
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import org.koin.ktor.ext.get

const val TAG = "feature"

fun Route.featureRoutes() {
    rpc("/feature") {
        Log.tag(TAG).i { "Feature endpoint triggered" }
        rpcConfig {
            serialization {
                json()
            }
        }

        registerService<FeatureService> { get() }
    }
}
