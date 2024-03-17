package com.example.weather.Utility

import com.example.weather.Pojo.FavoriteLocation
import com.example.weather.Pojo.WeatherResponse

sealed class Utility {
    class Success(val data :WeatherResponse):Utility()
    class Successful(val fav: List<FavoriteLocation>):Utility()
    class Failure(val msg:Throwable):Utility()
    object Loading:Utility()
}