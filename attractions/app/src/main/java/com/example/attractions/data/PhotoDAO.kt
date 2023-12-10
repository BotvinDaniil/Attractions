package com.example.attractions.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDAO {
    @Query("SELECT * FROM photoInfo")
    fun getAllPhoto(): Flow<List<PhotoInfo>>

    @Insert
     fun addPhoto(photo: PhotoInfo)



}

