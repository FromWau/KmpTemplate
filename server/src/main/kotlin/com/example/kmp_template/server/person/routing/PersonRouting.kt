package com.example.kmp_template.server.person.routing

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_rpc.person.PeopleService
import io.ktor.server.routing.Route
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import org.koin.ktor.ext.get

const val TAG = "PersonRouting"

fun Route.personRoutes() {
    rpc("/person") {
        Log.tag(TAG).i { "/person endpoint triggered" }
        rpcConfig {
            serialization {
                json()
            }
        }

        registerService<PeopleService> { get() }
    }
}
