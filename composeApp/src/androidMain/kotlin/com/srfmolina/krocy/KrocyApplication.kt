package com.srfmolina.krocy

import android.app.Application
import com.srfmolina.krocy.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class KrocyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@KrocyApplication)
            androidLogger() //TODO: silence this in release
        }
    }
}