package com.didik.canvassku.data.local.db

import androidx.room.TypeConverter
import com.didik.canvassku.domain.model.SyncStatus

class Converters {
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}
