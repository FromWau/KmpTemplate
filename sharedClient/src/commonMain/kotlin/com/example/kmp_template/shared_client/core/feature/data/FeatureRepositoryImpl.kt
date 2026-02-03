package com.example.kmp_template.shared_client.core.feature.data

import com.example.kmp_template.core.result.EmptyResult
import com.example.kmp_template.core.result.Result
import com.example.kmp_template.shared_client.core.feature.data.mapper.toDomain
import com.example.kmp_template.shared_client.core.feature.data.mapper.toRpc
import com.example.kmp_template.shared_client.core.feature.data.rpc.FeatureRpcClient
import com.example.kmp_template.shared_client.core.feature.domain.model.Model
import com.example.kmp_template.shared_client.core.feature.domain.repository.FeatureRepoError
import com.example.kmp_template.shared_client.core.feature.domain.repository.FeatureRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class FeatureRepositoryImpl(
    private val featureRpcClient: FeatureRpcClient,
) : FeatureRepository {
    override suspend fun saveModel(model: Model): EmptyResult<FeatureRepoError.SaveError> {
        try {
            val saved = featureRpcClient.saveModel(model.toRpc())
            return if (saved) {
                Result.Success(Unit)
            } else {
                Result.Error(FeatureRepoError.SaveError.GenericError(Exception("Failed to save model via RPC")))
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(FeatureRepoError.SaveError.GenericError(e))
        }
    }

    override suspend fun getModelByName(name: String): Result<Model, FeatureRepoError.GetModelError> {
        try {
            val foundModel = featureRpcClient.getModelByName(name)
            return if (foundModel != null) {
                Result.Success(foundModel.toDomain())
            } else {
                Result.Error(FeatureRepoError.GetModelError.NotFound(name))
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(FeatureRepoError.GetModelError.GenericError(e))
        }
    }
}