package com.didik.canvassku.core.di

import android.content.Context
import androidx.work.WorkManager
import org.koin.core.annotation.Singleton

@Singleton
fun provideWorkManager(context: Context): WorkManager =
    WorkManager.getInstance(context.applicationContext)
