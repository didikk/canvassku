package com.didik.canvassku.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.didik.canvassku.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class StoreDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var storeDao: StoreDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        storeDao = database.storeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeAll_emitsUpsertedStoresOrderedByName() = runTest {
        storeDao.upsertAll(
            listOf(
                StoreEntity(id = "2", name = "Beta Toko", address = null, ownerName = null),
                StoreEntity(id = "1", name = "Alpha Toko", address = "Jl. A", ownerName = "Budi"),
            ),
        )

        val stores = storeDao.observeAll().first()

        assertEquals(listOf("Alpha Toko", "Beta Toko"), stores.map { it.name })
    }
}
