package com.example.kmp_template.server.feature.data.database.mapper

import com.example.kmp_template.server.feature.data.database.model.ModelEntity
import com.example.kmp_template.server.feature.domain.model.Model

fun Model.toEntity(): ModelEntity = ModelEntity(id = this.id, name = this.name)

fun ModelEntity.toDomain(): Model = Model(id = this.id, name = this.name)