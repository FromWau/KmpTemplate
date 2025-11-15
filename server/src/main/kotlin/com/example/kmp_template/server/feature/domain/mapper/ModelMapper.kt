package com.example.kmp_template.server.feature.domain.mapper

import com.example.kmp_template.server.feature.domain.model.Model
import com.example.kmp_template.shared_rpc.feature.model.ModelRpc

fun Model.toRpc(): ModelRpc =
    ModelRpc(
        id = this.id,
        name = this.name,
    )

fun ModelRpc.toDomain(): Model = Model(
    id = this.id,
    name = this.name,
)