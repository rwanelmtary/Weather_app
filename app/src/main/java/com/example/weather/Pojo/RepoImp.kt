package com.example.weather.Pojo

import com.example.weather.DB.LocalDataSource
import com.example.weather.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepoImp (private val remoteDataSource: RemoteDataSource,
private var localDataSource: LocalDataSource):Repo{

    companion object{
        private var instance : RepoImp ?= null
        fun getInstance(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource

            ):RepoImp{
            return instance ?: synchronized(this){
                val result = RepoImp(remoteDataSource,localDataSource).also { instance = it }
                instance = result
                result
            }
        }
    }
    override suspend fun getWeather(latitude: Double, longitude: Double, language: String?, unit: String?): Flow<WeatherResponse> {
//        val lang = null
//        val unit = null
        return flowOf( remoteDataSource.getForecast(latitude, longitude,language ,unit))
    }

    override suspend fun getFavourites(): Flow<List<FavoriteLocation>> {
        return localDataSource.getStoredFavLocation()
    }

    override suspend fun insertToFav(favoriteLocation: FavoriteLocation) {
       localDataSource.insertLocation(favoriteLocation)
    }

    override suspend fun deletFromFav(favoriteLocation: FavoriteLocation) {
     localDataSource.deletLocation(favoriteLocation)
    }
}

