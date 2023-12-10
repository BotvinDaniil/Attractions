package com.example.attractions.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PhotoInfo::class], version = 1, exportSchema = false)
abstract class PhotoDataBase : RoomDatabase() {
    abstract fun photoDao(): PhotoDAO
}