package com.example.kmp_template.server.feature.domain.repository

import com.example.kmp_template.server.feature.domain.model.Model
import kotlinx.coroutines.flow.Flow

interface FeatureRepository {
    fun getAllModelsFlow(): Flow<List<Model>>
    suspend fun getModelByName(name: String): Model?

    suspend fun saveModel(model: Model): Boolean
}
