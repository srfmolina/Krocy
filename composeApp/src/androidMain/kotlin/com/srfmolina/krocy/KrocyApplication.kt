package com.srfmolina.krocy

import android.app.Application
import com.srfmolina.krocy.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class KrocyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@KrocyApplication)  // contexto Android para repos, prefs, etc.
            androidLogger()                         // logs de Koin en Logcat (solo debug)
        }
    }
}