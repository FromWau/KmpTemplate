package com.example.kmp_template.shared_client.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.kmp_template.core.config.ConfigManager
import com.example.kmp_template.core.database.DatabaseFactory
import com.example.kmp_template.shared_client.app.AppStartupViewModel
import com.example.kmp_template.shared_client.config.AppConfigProvider
import com.example.kmp_template.shared_client.config.AppConfigProviderFactory
import com.example.kmp_template.shared_client.core.feature.data.FeatureRepositoryImpl
import com.example.kmp_template.shared_client.core.feature.data.rpc.FeatureRpcClient
import com.example.kmp_template.shared_client.core.feature.domain.repository.FeatureRepository
import com.example.kmp_template.shared_client.core.navigation.NavigationService
import com.example.kmp_template.shared_client.core.toast.ToastService
import com.example.kmp_template.shared_client.home.presentation.HomeViewModel
import com.example.kmp_template.shared_client.setting.data.SettingRepositoryImpl
import com.example.kmp_template.shared_client.setting.data.database.SettingDao
import com.example.kmp_template.shared_client.setting.data.database.SettingDatabase
import com.example.kmp_template.shared_client.setting.domain.repository.SettingRepository
import com.example.kmp_template.shared_client.setting.presentation.SettingViewModel
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

    // Setting
    single<SettingDatabase> {
        get<DatabaseFactory>().create<SettingDatabase>(dbname = SettingDatabase.DB_NAME)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<SettingDao> { get<SettingDatabase>().settingDao }
    singleOf(::SettingRepositoryImpl) bind SettingRepository::class

    // Feature (RPC example)
    singleOf(::FeatureRpcClient)
    singleOf(::FeatureRepositoryImpl) bind FeatureRepository::class
}


val viewModelModules = module {
    viewModelOf(::AppStartupViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingViewModel)
}


expect val sharedPlatformModules: Module
