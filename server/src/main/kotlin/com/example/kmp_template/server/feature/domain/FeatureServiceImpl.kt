package com.example.kmp_template.server.feature.domain

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.server.feature.domain.mapper.toDomain
import com.example.kmp_template.server.feature.domain.mapper.toRpc
import com.example.kmp_template.server.feature.domain.repository.FeatureRepository
import com.example.kmp_template.shared_rpc.feature.FeatureService
import com.example.kmp_template.shared_rpc.feature.model.ModelRpc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeatureServiceImpl(
    private val repo: FeatureRepository,
) : FeatureService {
    companion object {
        private const val TAG = "FeatureService"
    }

    override fun getAllModelsFlow(): Flow<List<ModelRpc>> {
        Log.tag(TAG).d { "getAllModelsFlow called" }
        return repo.getAllModelsFlow().map { allModels ->
            allModels.map { it.toRpc() }
        }
    }

    override suspend fun getModelByName(name: String): ModelRpc? {
        Log.tag(TAG).d { "getModelByName called with name: $name" }
        return repo.getModelByName(name)?.toRpc()
    }

    override suspend fun saveModel(model: ModelRpc): Boolean {
        Log.tag(TAG).d { "saveModel called with model: $model" }
        return repo.saveModel(model.toDomain())
    }
}