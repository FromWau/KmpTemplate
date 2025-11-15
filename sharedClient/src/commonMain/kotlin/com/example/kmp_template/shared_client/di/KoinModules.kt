package com.example.kmp_template.shared_client.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.kmp_template.core.database.DatabaseFactory
import com.example.kmp_template.shared_client.feature.data.FeatureRepositoryImpl
import com.example.kmp_template.shared_client.feature.data.database.FeatureDatabase
import com.example.kmp_template.shared_client.feature.data.database.ModelDao
import com.example.kmp_template.shared_client.feature.domain.repository.FeatureRepository
import com.example.kmp_template.shared_client.feature.presentation.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModules = module {
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
}


val viewModelModules = module {
    viewModelOf(::HomeViewModel)
}
