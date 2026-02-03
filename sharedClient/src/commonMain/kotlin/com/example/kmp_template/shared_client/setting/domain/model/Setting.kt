package com.example.kmp_template.shared_client.setting.domain.model

data class Setting(
    val settings: List<Setting>,
) {
    data class Setting(
        val key: String,
        val value: String,
    )
}
