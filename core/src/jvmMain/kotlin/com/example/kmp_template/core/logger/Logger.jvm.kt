package com.example.kmp_template.core.logger

actual val Log: Logger
    get() = object : Logger {
        override fun log(tag: String, logLevel: LogLevel, lazyMessage: () -> String) {
            println(toLogString(tag, logLevel, lazyMessage))
        }

        override fun log(tag: String, logLevel: LogLevel, throwable: Throwable) {
            println(toLogString(tag, logLevel, throwable))
        }
    }