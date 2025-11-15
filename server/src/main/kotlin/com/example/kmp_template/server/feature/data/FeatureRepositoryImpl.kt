package com.example.kmp_template.server.feature.data

import com.example.kmp_template.server.feature.data.database.dao.ModelDao
import com.example.kmp_template.server.feature.data.database.mapper.toDomain
import com.example.kmp_template.server.feature.domain.model.Model
import com.example.kmp_template.server.feature.domain.repository.FeatureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeatureRepositoryImpl(
    private val modelDao: ModelDao,
) : FeatureRepository {
    override fun getAllModelsFlow(): Flow<List<Model>> {
        return modelDao.getAllModelsFlow().map { allModels ->
            allModels.map { it.toDomain() }
        }
    }

    override suspend fun getModelByName(name: String): Model? {
        return modelDao.findByName(name)?.toDomain()
    }
}