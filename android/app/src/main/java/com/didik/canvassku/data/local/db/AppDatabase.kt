package com.didik.canvassku.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.didik.canvassku.data.local.entity.StoreEntity
import com.didik.canvassku.data.local.entity.VisitReportEntity

@Database(
    entities = [StoreEntity::class, VisitReportEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun visitReportDao(): VisitReportDao
}
