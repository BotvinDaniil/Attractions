package com.example.attractions.data.placesApi.InfoDTO


import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("city")
    val city: String,
    @SerializedName("house_number")
    val houseNumber: String,
     @SerializedName("road")
    val road: String
    )