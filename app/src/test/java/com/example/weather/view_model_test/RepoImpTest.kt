package com.example.weather.view_model_test

import com.example.weather.DB.LocalDataSource
import com.example.weather.Pojo.Current
import com.example.weather.Pojo.FavoriteLocation
import com.example.weather.Pojo.RepoImp
import com.example.weather.network.RemoteDataSource
import com.example.weather.Pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

class RepoImpTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource

        private lateinit var repo: FakeRepoDataSource

        @Before
        fun setUp() {
            repo = FakeRepoDataSource()
        }

        @Test
        fun testGetWeather() {
            // Given
            val latitude = 43.77
            val longitude = 35.89
            val expectedWeatherResponse = WeatherResponse(/* Initialize with test data */)
            repo.setWeatherResponse(expectedWeatherResponse)

            // When
            val actualResponse = runBlocking {
                repo.getForecast(latitude, longitude, "en", "metric")
            }

            // Then
            assertThat(actualResponse, `is`(notNullValue()))
            assertThat(actualResponse, `is`(expectedWeatherResponse))
        }

    @Test
    fun testGetFavourites() {
        // Given
        val expectedLocation = FavoriteLocation(
            id = 1,
            latitude = 43.77,
            longitude = 35.89,
            address_en = "Test Address (EN)",
            address_ar = "Test Address (AR)"
        )

        // When
        runBlocking { repo.insertLocation(expectedLocation) }
        val actualFavoriteLocations = runBlocking { repo.getStoredFavLocation().first() }

        // Then
        assertThat(actualFavoriteLocations.size, `is`(1))
        assertThat(actualFavoriteLocations[0], `is`(expectedLocation))

    }

    @Test
    fun testInsertAndGetFavoriteLocations() {
        // Given
        val expectedLocation = FavoriteLocation(/* Initialize with test data */)

        // When
        runBlocking { repo.insertLocation(expectedLocation) }
        val actualLocations = runBlocking { repo.getStoredFavLocation().first() }

        // Then
        assertThat(actualLocations.size, `is`(1))
        assertThat(actualLocations[0], `is`(expectedLocation))
    }
}




class FakeRepoDataSource : RemoteDataSource, LocalDataSource {

    private val favoriteLocations = mutableListOf<FavoriteLocation>()
    private var weatherResponse: WeatherResponse? = null

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        lang: String?,
        units: String?
    ): WeatherResponse {
        return weatherResponse ?: throw IllegalStateException("Weather response not set")
    }

    override suspend fun insertLocation(favoriteLocation: FavoriteLocation) {
        favoriteLocations.add(favoriteLocation)
    }

    override suspend fun deletLocation(favoriteLocation: FavoriteLocation) {
        favoriteLocations.remove(favoriteLocation)
    }

    override fun getStoredFavLocation(): Flow<List<FavoriteLocation>> {
        return flow { emit(favoriteLocations) }
    }

    fun setWeatherResponse(weatherResponse: WeatherResponse) {
        this.weatherResponse = weatherResponse
    }
}

