package com.example.weather.network

import com.example.weather.Pojo.WeatherData
import com.example.weather.Pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSourceImp : RemoteDataSource {

    private val weatherService: WeatherService =
        RetrofitHelper.retrofitInstance.create(WeatherService::class.java)

    object RetrofitHelper {
        val BASE_URL = "https://api.openweathermap.org/data/3.0/"
        val apiKey = //"40dac0af7018969cbb541943f944ba29"
            "ac8f7773e892b0d6eb9c4de163756d21"
        val retrofitInstance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        lang: String?,
        units: String?,
    ): WeatherResponse {
        return weatherService.getForecast(
            latitude = latitude,
            longitude = longitude,
            lang = lang,
            units = units,
            apiKey = RetrofitHelper.apiKey
        )
    }

}



//    override suspend fun getForecast(latitude: Double, longitude: Double): Flow<WeatherData> {
//        return flow {
//            val response = weatherService.getForecast(latitude, longitude, RetrofitHelper.apiKey)
//            if (response.isSuccessful) {
//                val weatherData = response.body()
//                if (weatherData != null) {
//                    emit(weatherData)
//                } else {
//                }
//
//            }





