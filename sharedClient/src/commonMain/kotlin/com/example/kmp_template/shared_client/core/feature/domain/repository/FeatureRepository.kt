package com.example.kmp_template.shared_client.core.feature.domain.repository

import com.example.kmp_template.core.result.EmptyResult
import com.example.kmp_template.core.result.Error
import com.example.kmp_template.core.result.Result
import com.example.kmp_template.shared_client.core.feature.domain.model.Model

interface FeatureRepository {
    suspend fun saveModel(model: Model): EmptyResult<FeatureRepoError.SaveError>

    suspend fun getModelByName(name: String): Result<Model, FeatureRepoError.GetModelError>
}

sealed interface FeatureRepoError {
    sealed interface SaveError : FeatureRepoError, Error {
        data class GenericError(val throwable: Throwable): SaveError
    }

    sealed interface GetModelError : FeatureRepoError, Error {
        data class NotFound(val name: String): GetModelError
        data class GenericError(val throwable: Throwable): GetModelError
    }
}