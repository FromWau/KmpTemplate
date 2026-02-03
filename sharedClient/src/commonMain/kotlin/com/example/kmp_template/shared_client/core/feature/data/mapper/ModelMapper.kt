package com.example.kmp_template.shared_client.core.feature.data.mapper

import com.example.kmp_template.shared_client.core.feature.domain.model.Model
import com.example.kmp_template.shared_rpc.feature.model.ModelRpc

fun Model.toRpc(): ModelRpc {
    return ModelRpc(
        id = this.id,
        name = this.name,
    )
}

fun ModelRpc.toDomain(): Model {
    return Model(
        id = this.id,
        name = this.name,
    )
}