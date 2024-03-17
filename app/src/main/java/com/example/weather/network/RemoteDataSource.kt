package com.example.weather.network

import com.example.weather.Pojo.WeatherData
import com.example.weather.Pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getForecast ( latitude: Double,
    longitude: Double,
    lang: String?,
    units: String?,
    ):WeatherResponse
}