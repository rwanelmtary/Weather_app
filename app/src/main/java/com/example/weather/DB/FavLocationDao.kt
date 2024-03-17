package com.example.weather.DB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.Pojo.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavLocationDao {
    @Query("SELECT * FROM favoriteLocation")
    fun getAllFavLocation(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavLocation(favoriteLocation: FavoriteLocation)

    @Delete
    suspend fun deletLocation(favoriteLocation: FavoriteLocation)
}
