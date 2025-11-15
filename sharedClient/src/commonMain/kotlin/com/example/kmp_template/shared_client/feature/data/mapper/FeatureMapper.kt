package com.example.kmp_template.shared_client.feature.data.mapper

import com.example.kmp_template.shared_client.feature.data.database.ModelEntity
import com.example.kmp_template.shared_client.feature.domain.model.Model

fun Model.toEntity(): ModelEntity =
    ModelEntity(id = this.id, name = this.name)

fun ModelEntity.toDomain(): Model =
    Model(id = this.id, name = this.name)