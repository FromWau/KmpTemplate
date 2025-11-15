package com.example.kmp_template.shared_client.feature.presentation

import com.example.kmp_template.shared_client.feature.domain.model.Model

data class HomeState(
    val models: List<Model>? = null,
)