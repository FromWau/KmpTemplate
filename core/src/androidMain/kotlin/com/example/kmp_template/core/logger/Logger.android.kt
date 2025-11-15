package com.example.kmp_template.core.logger

import android.util.Log as AndroidLog

actual val Log: Logger
    get() = object : Logger {
        override fun log(tag: String, logLevel: LogLevel, lazyMessage: () -> String) {
            when (logLevel) {
                LogLevel.VERBOSE -> AndroidLog.v(tag, lazyMessage())
                LogLevel.DEBUG -> AndroidLog.d(tag, lazyMessage())
                LogLevel.INFO -> AndroidLog.i(tag, lazyMessage())
                LogLevel.WARN -> AndroidLog.w(tag, lazyMessage())
                LogLevel.ERROR -> AndroidLog.e(tag, lazyMessage())
            }
        }

        override fun log(tag: String, logLevel: LogLevel, throwable: Throwable) {
            when (logLevel) {
                LogLevel.VERBOSE -> AndroidLog.v(tag, throwable.message, throwable)
                LogLevel.DEBUG -> AndroidLog.d(tag, throwable.message, throwable)
                LogLevel.INFO -> AndroidLog.i(tag, throwable.message, throwable)
                LogLevel.WARN -> AndroidLog.w(tag, throwable.message, throwable)
                LogLevel.ERROR -> AndroidLog.e(tag, throwable.message, throwable)
            }
        }
    }
