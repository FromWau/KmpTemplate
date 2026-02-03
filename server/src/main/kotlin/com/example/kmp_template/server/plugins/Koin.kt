package com.example.kmp_template.server.plugins

import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.di.coreModule
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.server.config.ServerConfig
import com.example.kmp_template.server.feature.data.FeatureRepositoryImpl
import com.example.kmp_template.server.feature.data.database.FeatureDatabase
import com.example.kmp_template.server.feature.data.database.dao.ModelDao
import com.example.kmp_template.server.feature.data.database.getFeatureDatabase
import com.example.kmp_template.server.feature.domain.FeatureServiceImpl
import com.example.kmp_template.server.feature.domain.repository.FeatureRepository
import com.example.kmp_template.shared_rpc.feature.FeatureService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.core.logger.Logger as KoinLogger

fun serverModule(config: ServerConfig) = module {
    single<ServerConfig> { config }

    single<FeatureDatabase> { getFeatureDatabase(get<SystemAppDirectories>().dataDir()) }
    single<ModelDao> { get<FeatureDatabase>().modelDao() }

    singleOf(::FeatureRepositoryImpl) bind FeatureRepository::class
    singleOf(::FeatureServiceImpl) bind FeatureService::class
}

fun Application.configureKoin(config: ServerConfig) {
    install(Koin) {
        logger(KoinLoggerAdapter(Level.DEBUG))
        modules(coreModule, serverModule(config))
    }
}

class KoinLoggerAdapter(level: Level = Level.INFO) : KoinLogger(level) {
    private val logger = Log.tag("Koin")

    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.v { msg }
            Level.INFO -> logger.d { msg }
            Level.WARNING -> logger.w { msg }
            Level.ERROR -> logger.e { msg }
            else -> logger.e { msg }
        }
    }
}
