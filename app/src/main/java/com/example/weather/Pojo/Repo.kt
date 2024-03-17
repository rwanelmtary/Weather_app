package com.example.weather.Pojo

import kotlinx.coroutines.flow.Flow

interface Repo {
    suspend fun getWeather(latitude: Double, longitude: Double, language: String?, unit: String?): Flow<WeatherResponse>
    suspend fun getFavourites(): Flow<List<FavoriteLocation>>
    suspend fun insertToFav(favoriteLocation: FavoriteLocation)
    suspend fun deletFromFav(favoriteLocation: FavoriteLocation)
}


