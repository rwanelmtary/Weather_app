package com.example.weather.Model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.Pojo.Repo

class WeatherModelFactory (val repo: Repo,val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            WeatherViewModel(repo,context) as T
        } else {
            throw IllegalAccessException("ViewModel class not found")
        }
    }
    }



