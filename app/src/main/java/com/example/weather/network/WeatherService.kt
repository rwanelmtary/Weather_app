package com.example.weather.network

import com.example.weather.Pojo.WeatherData
import com.example.weather.Pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String?,
        @Query("units") units: String?,
       // @Query("exclude") exclude: String,
        @Query("appid") apiKey: String
    ):WeatherResponse
}
