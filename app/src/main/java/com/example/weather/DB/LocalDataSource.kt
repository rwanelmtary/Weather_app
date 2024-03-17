package com.example.weather.DB

import com.example.weather.Pojo.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insertLocation(favoriteLocation: FavoriteLocation)
    suspend fun deletLocation(favoriteLocation: FavoriteLocation)
    fun getStoredFavLocation() : Flow<List<FavoriteLocation>>
}