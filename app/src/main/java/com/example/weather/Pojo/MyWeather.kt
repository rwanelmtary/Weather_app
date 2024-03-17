package com.example.weather.Pojo


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//@Entity(tableName = "weatherResponse",primaryKeys = ["isFav"])
//@TypeConverters(WeatherTypeConverter::class)
data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezoneOffset: Int,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
    val alerts: List<Alert>,
    val isFav: Int = 0,
    var address: String?=null ,
) {
    constructor() : this(0.0, 0.0, "", 0, Current(), listOf(), listOf(), listOf(),0,"")
}
data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int?,
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val windDeg: Int,
    val windGust: Double?,
    val weather: List<Weather>,
) {
    constructor() : this(0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0, null, listOf())
}

@Entity(tableName = "favoriteLocation")
data class FavoriteLocation (
    @PrimaryKey(autoGenerate = true)
    @SerializedName("fav")
    var id: Long = 0,
    var latitude: Double,
    var longitude: Double,
    var address_en: String,
    var address_ar: String,
):Serializable{
    constructor() : this(0,0.0,0.0,"","")
}
//@Entity(tableName = "myAlert")
data class MyAlert(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var dateOfNotification: Long,
    var event: String,
    var startTime: Long,
    var endTime: Long,
    var description: String,
){
    constructor() : this(0,0,"",0,0,"")
}

data class Minutely(
    val dt: Long,
    val precipitation: Double,
)

data class Hourly(
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>,
    val pop: Double,
)

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moon_phase: Double,
    val temp: Temp?,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val uvi: Double,
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double,
)

data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double,
)

data class Alert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>,
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

class WeatherTypeConverter {
    @TypeConverter
    fun fromWeatherResponseToString(weatherResponse: WeatherResponse) = Gson().toJson(weatherResponse)
    @TypeConverter
    fun fromStringToWeatherResponse(weatherString : String) = Gson().fromJson(weatherString, WeatherResponse::class.java)

    @TypeConverter
    fun fromCurrentToString(current: Current) = Gson().toJson(current)
    @TypeConverter
    fun fromStringToCurrent(currentString : String) = Gson().fromJson(currentString, Current::class.java)

    @TypeConverter
    fun fromHourlyToString(hourly: List<Hourly>) = Gson().toJson(hourly)
    @TypeConverter
    fun fromStringToHourly(hourlyString : String) = Gson().fromJson(hourlyString, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun fromDailyToString(daily: List<Daily>) = Gson().toJson(daily)
    @TypeConverter
    fun fromStringToDaily(dailyString : String) = Gson().fromJson(dailyString, Array<Daily>::class.java).toList()

    @TypeConverter
    fun fromTempToString(temp: Temp) = Gson().toJson(temp)
    @TypeConverter
    fun fromStringToTemp(tempString : String) = Gson().fromJson(tempString, Temp::class.java)

    @TypeConverter
    fun fromFeelsLikeToString(feelsLike: FeelsLike) = Gson().toJson(feelsLike)
    @TypeConverter
    fun fromStringToFeelsLike(feelLiksString : String) = Gson().fromJson(feelLiksString, FeelsLike::class.java)

    @TypeConverter
    fun fromWeatherToString(weather: List<Weather>) = Gson().toJson(weather)
    @TypeConverter
    fun fromStringToWeather(weatherString : String) = Gson().fromJson(weatherString, Array<Weather>::class.java).toList()

    @TypeConverter
    fun fromAlertToString(alert: List<Alert>) = Gson().toJson(alert)
    @TypeConverter
    fun fromStringToAlerts(alertString : String) = Gson().fromJson(alertString, Array<Alert>::class.java).toList()

}


