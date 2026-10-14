package com.didik.canvassku.core.di

import android.content.Context
import androidx.room.Room
import com.didik.canvassku.data.local.db.AppDatabase
import com.didik.canvassku.data.local.db.StoreDao
import com.didik.canvassku.data.local.db.VisitReportDao
import com.didik.canvassku.data.local.preferences.AuthPreferences
import com.didik.canvassku.data.local.preferences.authPreferencesDataStore
import org.koin.core.annotation.Singleton

@Singleton
fun provideAppDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "canvassku.db",
    ).build()

@Singleton
fun provideStoreDao(database: AppDatabase): StoreDao = database.storeDao()

@Singleton
fun provideVisitReportDao(database: AppDatabase): VisitReportDao = database.visitReportDao()

@Singleton
fun provideAuthPreferences(context: Context): AuthPreferences =
    AuthPreferences(authPreferencesDataStore(context))
