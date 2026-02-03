package com.example.kmp_template.server.plugins

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.kmp_template.core.database.DatabaseFactory
import com.example.kmp_template.core.di.coreModule
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.server.config.ServerConfig
import com.example.kmp_template.server.person.data.PersonRepositoryImpl
import com.example.kmp_template.server.person.data.database.PersonDatabase
import com.example.kmp_template.server.person.data.database.dao.PersonDao
import com.example.kmp_template.server.person.domain.PeopleServiceImpl
import com.example.kmp_template.server.person.domain.repository.PersonRepository
import com.example.kmp_template.shared_rpc.person.PeopleService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.coroutines.Dispatchers
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.core.logger.Logger as KoinLogger

fun serverModule(config: ServerConfig) = module {
    single<ServerConfig> { config }

    // Person
    single<PersonDatabase> {
        val dbFile = "${PersonDatabase.DB_NAME}.db"
        get<DatabaseFactory>().create<PersonDatabase>(dbname = dbFile)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<PersonDao> { get<PersonDatabase>().personDao() }
    singleOf(::PersonRepositoryImpl) bind PersonRepository::class

    // Services
    singleOf(::PeopleServiceImpl) bind PeopleService::class
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
