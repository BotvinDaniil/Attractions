package com.example.attractions.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photoInfo")
data class PhotoInfo(
    @PrimaryKey()
    val photoUri: String,
    val photoDate: String
)

