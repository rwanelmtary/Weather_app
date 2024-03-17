package com.example.weather.settings

import android.content.Context


object PreferenceManager {

    private const val PREF_NAME = "settings"
    private const val KEY_SELECTED_LANGUAGE = "selectedLanguage"
    private const val KEY_SELECTED_TEMPERATURE_UNIT = "selectedTemperatureUnit"
    private const val KEY_SELECTED_LOCATION = "selectedLocation"
    private const val DEFAULT_LANGUAGE = "English"
    private const val DEFAULT_TEMPERATURE_UNIT = "Celsius"
    private const val DEFAULT_LOCATION = "None"

    fun saveSelectedLanguage(context: Context, language: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SELECTED_LANGUAGE, language)
        editor.apply()
    }

    fun saveSelectedTemperatureUnit(context: Context, temperatureUnit: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SELECTED_TEMPERATURE_UNIT, temperatureUnit)
        editor.apply()
    }
    fun getSelectedTemperatureUnit(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_SELECTED_TEMPERATURE_UNIT, Constants.UNITS_CELSIUS) ?: Constants.UNITS_CELSIUS
    }


    fun getSelectedLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_SELECTED_LANGUAGE, Constants.LANGUAGE_EN) ?: Constants.LANGUAGE_EN
    }


    fun saveSelectedLocation(context: Context, location: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SELECTED_LOCATION, location)
        editor.apply()
    }

    fun getSelectedLocation(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_SELECTED_LOCATION, Constants.LOCATION_GPS) ?: DEFAULT_LOCATION
        }
}
