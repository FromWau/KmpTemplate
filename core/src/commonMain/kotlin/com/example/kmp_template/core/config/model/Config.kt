package com.example.kmp_template.core.config.model

interface Config {
    val name: String

    // Default content to create the config file if it doesn't exist
    val defaultContent: String
}
