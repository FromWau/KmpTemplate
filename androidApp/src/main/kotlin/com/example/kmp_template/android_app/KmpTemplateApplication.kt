package com.example.kmp_template.android_app

import android.app.Application
import com.example.kmp_template.shared_client.di.initKoin
import org.koin.android.ext.koin.androidContext

class KmpTemplateApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@KmpTemplateApplication)
        }
    }
}