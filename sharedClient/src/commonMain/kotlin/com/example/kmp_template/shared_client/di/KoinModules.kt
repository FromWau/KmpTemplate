package com.example.kmp_template.shared_client.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.kmp_template.core.config.ConfigLoader
import com.example.kmp_template.core.database.DatabaseFactory
import com.example.kmp_template.shared_client.app.AppStartupViewModel
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

    // Config Loader
    singleOf(::ConfigLoader)

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
}


val viewModelModules = module {
    viewModelOf(::AppStartupViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingViewModel)
}


expect val sharedPlatformModules: Module
