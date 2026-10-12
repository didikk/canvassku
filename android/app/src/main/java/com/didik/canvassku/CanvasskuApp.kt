package com.didik.canvassku

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
class CanvasskuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<CanvasskuApp> {
            androidLogger(Level.ERROR)
            androidContext(this@CanvasskuApp)
        }
    }
}
