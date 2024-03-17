package com.example.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather.DB.FavLocationDB
import com.example.weather.DB.LocalDataSourceImp
import com.example.weather.Pojo.FavoriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: FavLocationDB
    private lateinit var localDataSource: LocalDataSourceImp

    @Before
    fun setUp() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavLocationDB::class.java
        ).allowMainThreadQueries().build()

        // Initialize the local data source
        localDataSource = LocalDataSourceImp(ApplicationProvider.getApplicationContext())
    }

    @After
    fun shutdown() {
        // Close the database after each test
        database.close()
    }

    @Test
    fun insertFavLocation() = runBlockingTest {
        // Given
        val location = FavoriteLocation(
            id = 1,
            latitude = 43.77,
            longitude = 35.89,
            address_en = "Sample Address EN",
            address_ar = "Sample Address AR"
        )

        // When
        localDataSource.insertLocation(location)

        // Then
        val result = localDataSource.getStoredFavLocation().first()
        assertThat(
            "Inserted location should exist in the result list",
            result.isNotEmpty(),
            `is`(true)
        )
        // Check if the inserted location matches the first element in the result list
        assertThat("Inserted location should match the first element in the result list", result[0], `is`(location))
    }

    @Test
    fun deleteFavLocation() = runBlockingTest {
        // Given
        val location = FavoriteLocation(
            id = 1,
            latitude = 43.77,
            longitude = 35.89,
            address_en = "Sample Address EN",
            address_ar = "Sample Address AR"
        )

        // Insert the location into the database first
        localDataSource.insertLocation(location)

        // When
        localDataSource.deletLocation(location)

        // Then
        val result = localDataSource.getStoredFavLocation().first()
        assertThat(
            "deleted location shouldn't exist in the result list",
            result.isEmpty(),
            `is`(true)
        )
    }

}
