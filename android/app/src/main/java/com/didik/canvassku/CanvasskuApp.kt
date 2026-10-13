package com.didik.canvassku

import android.app.Application
import androidx.work.Configuration
import com.didik.canvassku.data.worker.CanvasskuWorkerFactory
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
class CanvasskuApp : Application(), Configuration.Provider {
    override fun onCreate() {
        // Koin must be ready before WorkManager reads workManagerConfiguration.
        startKoin<CanvasskuApp> {
            androidLogger(Level.ERROR)
            androidContext(this@CanvasskuApp)
        }
        super.onCreate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(getKoin().get<CanvasskuWorkerFactory>())
            .build()
}
