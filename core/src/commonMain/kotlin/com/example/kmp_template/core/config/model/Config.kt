package com.example.kmp_template.core.config.model

interface Config {
    val name: String

    fun toToml(): String
}
