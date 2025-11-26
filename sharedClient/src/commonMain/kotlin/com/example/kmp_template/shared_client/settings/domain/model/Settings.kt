package com.example.kmp_template.shared_client.settings.domain.model

data class Settings(
    val settings: List<Setting>,
) {
    data class Setting(
        val key: String,
        val value: String,
    )
}
