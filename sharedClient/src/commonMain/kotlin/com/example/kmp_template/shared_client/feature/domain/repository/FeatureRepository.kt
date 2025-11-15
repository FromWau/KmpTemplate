package com.example.kmp_template.shared_client.feature.domain.repository

import com.example.kmp_template.shared_client.feature.domain.model.Model
import kotlinx.coroutines.flow.Flow

interface FeatureRepository {
    suspend fun insert(model: Model)

    suspend fun getAllModels(): List<Model>

    fun getAllModelsFlow(): Flow<List<Model>>
}