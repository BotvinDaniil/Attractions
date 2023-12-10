package com.example.attractions.data.placesApi.InfoDTO


import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double
)