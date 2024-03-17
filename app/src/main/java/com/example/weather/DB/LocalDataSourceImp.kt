package com.example.weather.DB

import android.content.Context
import com.example.weather.Pojo.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImp(context: Context): LocalDataSource {
    private var dao : FavLocationDao = FavLocationDB.getInstance(context).getLocationDao()
    override suspend fun insertLocation(favoriteLocation: FavoriteLocation) {
        dao.insertFavLocation(favoriteLocation)
    }

    override suspend fun deletLocation(favoriteLocation: FavoriteLocation) {
     dao.deletLocation(favoriteLocation)
    }

    override fun getStoredFavLocation(): Flow<List<FavoriteLocation>> {
      return dao.getAllFavLocation()
    }
}