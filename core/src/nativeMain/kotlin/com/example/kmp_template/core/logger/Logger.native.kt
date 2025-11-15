package com.example.kmp_template.core.logger

import platform.Foundation.NSLog

actual val Log: Logger
    get() = object : Logger {
        override fun log(tag: String, logLevel: LogLevel, lazyMessage: () -> String) {
            NSLog(toLogString(tag, logLevel, lazyMessage))
        }

        override fun log(tag: String, logLevel: LogLevel, throwable: Throwable) {
            NSLog(toLogString(tag, logLevel, throwable))
        }
    }