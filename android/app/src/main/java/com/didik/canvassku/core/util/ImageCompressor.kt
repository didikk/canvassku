package com.didik.canvassku.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.max

@Singleton
class ImageCompressor(private val context: Context) {
    suspend fun compressToFilesDir(
        sourceFile: File,
        maxSidePx: Int = 1280,
        quality: Int = 80,
    ): File = withContext(Dispatchers.Default) {
        val visitsDir = File(context.filesDir, "visits").apply { mkdirs() }
        val destination = File(visitsDir, "${UUID.randomUUID()}.jpg")

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(sourceFile.absolutePath, bounds)
        val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxSidePx)

        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val decoded = BitmapFactory.decodeFile(sourceFile.absolutePath, options)
            ?: error("Gagal membaca foto.")

        val scaled = scaleDown(decoded, maxSidePx)
        if (scaled !== decoded) {
            decoded.recycle()
        }

        FileOutputStream(destination).use { output ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
        }
        scaled.recycle()
        destination
    }

    private fun scaleDown(bitmap: Bitmap, maxSidePx: Int): Bitmap {
        val longest = max(bitmap.width, bitmap.height)
        if (longest <= maxSidePx) return bitmap
        val ratio = maxSidePx.toFloat() / longest.toFloat()
        val width = (bitmap.width * ratio).toInt().coerceAtLeast(1)
        val height = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        return bitmap.scale(width, height)
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxSidePx: Int): Int {
        var sample = 1
        val longest = max(width, height)
        while (longest / sample > maxSidePx * 2) {
            sample *= 2
        }
        return sample
    }
}
