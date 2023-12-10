package com.example.attractions.data.placesApi


import com.google.gson.annotations.SerializedName

data class InterrestingPlacesDto(
    @SerializedName("features")
    val features: List<Feature>,
    @SerializedName("type")
    val type: String
)