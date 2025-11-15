package com.example.kmp_template.shared_client.feature.data

import com.example.kmp_template.shared_client.feature.data.database.ModelDao
import com.example.kmp_template.shared_client.feature.data.mapper.toDomain
import com.example.kmp_template.shared_client.feature.data.mapper.toEntity
import com.example.kmp_template.shared_client.feature.domain.model.Model
import com.example.kmp_template.shared_client.feature.domain.repository.FeatureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeatureRepositoryImpl(
    private val modelDao: ModelDao,
) : FeatureRepository {
    override suspend fun insert(model: Model) {
        return modelDao.insert(model.toEntity())
    }

    override suspend fun getAllModels(): List<Model> {
        return modelDao.getAll().map { it.toDomain() }
    }

    override fun getAllModelsFlow(): Flow<List<Model>> {
        return modelDao.getAllFlow().map { models ->
            models.map { it.toDomain() }
        }
    }
}