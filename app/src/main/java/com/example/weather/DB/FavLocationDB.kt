package com.example.weather.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather.Pojo.FavoriteLocation

@Database(entities = [FavoriteLocation::class], version = 1)
abstract class FavLocationDB : RoomDatabase() {
    abstract fun getLocationDao(): FavLocationDao

    companion object {
        @Volatile
        private var INSTANCE: FavLocationDB? = null

        fun getInstance(context: Context): FavLocationDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavLocationDB::class.java, "favourite_location_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
