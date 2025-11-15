package com.example.kmp_template.shared_rpc.feature

import com.example.kmp_template.shared_rpc.feature.model.ModelRpc
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc

@Rpc
interface FeatureService {
    fun getAllModelsFlow(): Flow<List<ModelRpc>>
    suspend fun getModelByName(name: String): ModelRpc?
}
