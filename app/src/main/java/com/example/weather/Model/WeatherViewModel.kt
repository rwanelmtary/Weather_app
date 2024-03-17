package com.example.weather.Model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.Pojo.FavoriteLocation
import com.example.weather.Pojo.Repo
import com.example.weather.Utility.Utility
import com.example.weather.settings.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherViewModel(private var repo: Repo, private var context: Context) : ViewModel() {
    private val weather = MutableStateFlow<Utility>(Utility.Loading)
    val forcast = weather.asStateFlow()

    private val favouriteList = MutableStateFlow<Utility>(Utility.Loading)
    val favourite = favouriteList.asStateFlow()

    init {
        getAllFromDB()
    }

    fun getDayWeather(latitude: Double, longitude: Double, language: String?, unit: String?) =
        viewModelScope.launch(Dispatchers.IO) {
            if (isNetworkAvailable(context)) {
                repo.getWeather(
                    latitude,
                    longitude,
                    language = PreferenceManager.getSelectedLanguage(context),
                    unit = PreferenceManager.getSelectedTemperatureUnit(context)
                )
                    .catch { e -> weather.value = Utility.Failure(e) }
                    .collectLatest { data -> weather.value = Utility.Success(data) }
            } else {
                weather.value = Utility.Failure(OfflineException("No internet connection"))
            }
        }

    fun insertInDB(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertToFav(favoriteLocation)
        }
    }

    fun deletFromDB(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletFromFav(favoriteLocation)
        }
    }

    fun getAllFromDB() = viewModelScope.launch(Dispatchers.IO) {
        repo.getFavourites()
            .catch { e -> favouriteList.value = Utility.Failure(e) }
            .collectLatest { data -> favouriteList.value = Utility.Successful(data) }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    inner class OfflineException(message: String) : Exception(message)
}
