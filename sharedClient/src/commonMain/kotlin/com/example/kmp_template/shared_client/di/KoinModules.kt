package com.example.kmp_template.shared_client.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.kmp_template.core.database.DatabaseFactory
import com.example.kmp_template.shared_client.core.navigation.NavigationService
import com.example.kmp_template.shared_client.core.presentation.toast.ToastService
import com.example.kmp_template.shared_client.home.data.FeatureRepositoryImpl
import com.example.kmp_template.shared_client.home.data.database.FeatureDatabase
import com.example.kmp_template.shared_client.home.data.database.ModelDao
import com.example.kmp_template.shared_client.home.domain.repository.FeatureRepository
import com.example.kmp_template.shared_client.home.presentation.HomeViewModel
import com.example.kmp_template.shared_client.settings.data.SettingsRepositoryImpl
import com.example.kmp_template.shared_client.settings.data.database.SettingsDao
import com.example.kmp_template.shared_client.settings.data.database.SettingsDatabase
import com.example.kmp_template.shared_client.settings.domain.repository.SettingsRepository
import com.example.kmp_template.shared_client.settings.presentation.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModules = module {
    // Example
    single<FeatureDatabase> {
        get<DatabaseFactory>().create<FeatureDatabase>(dbname = FeatureDatabase.DB_NAME)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<ModelDao> { get<FeatureDatabase>().modelDao }
    singleOf(::FeatureRepositoryImpl) bind FeatureRepository::class

    // Navigation
    singleOf(::NavigationService)

    // Toast
    singleOf(::ToastService)

    // Settings
    single<SettingsDatabase> {
        get<DatabaseFactory>().create<SettingsDatabase>(dbname = SettingsDatabase.DB_NAME)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<SettingsDao> { get<SettingsDatabase>().settingsDao }
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
}


val viewModelModules = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
}
