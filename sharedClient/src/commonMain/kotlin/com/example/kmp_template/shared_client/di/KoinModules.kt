package com.example.kmp_template.shared_client.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.kmp_template.core.config.ConfigManager
import com.example.kmp_template.core.database.DatabaseFactory
import com.example.kmp_template.shared_client.app.AppStartupViewModel
import com.example.kmp_template.shared_client.config.AppConfigProvider
import com.example.kmp_template.shared_client.config.AppConfigProviderFactory
import com.example.kmp_template.shared_client.core.navigation.NavigationService
import com.example.kmp_template.shared_client.person.data.PersonRepositoryImpl
import com.example.kmp_template.shared_client.person.data.database.PersonDao
import com.example.kmp_template.shared_client.person.data.database.PersonDatabase
import com.example.kmp_template.shared_client.person.data.rpc.PersonRpcClient
import com.example.kmp_template.shared_client.person.domain.repository.PersonRepository
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.home.presentation.HomeViewModel
import com.example.kmp_template.shared_client.person.presentation.PersonViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModules = module {
    // Navigation
    singleOf(::NavigationService)

    // Toast
    singleOf(::ToastService)

    // Config
    singleOf(::ConfigManager)
    single<AppConfigProvider>(createdAtStart = true) {
        AppConfigProviderFactory.create(configManager = get(), systemDirs = get())
    }

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
    single<PersonDao> { get<PersonDatabase>().personDao }

    // Feature (RPC example)
    singleOf(::PersonRpcClient)
    singleOf(::PersonRepositoryImpl) bind PersonRepository::class
}


val viewModelModules = module {
    viewModelOf(::AppStartupViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::PersonViewModel)
}


expect val sharedPlatformModules: Module
