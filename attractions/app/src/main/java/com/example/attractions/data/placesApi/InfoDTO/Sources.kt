package com.example.attractions.data.placesApi.InfoDTO


import com.google.gson.annotations.SerializedName

data class Sources(
    @SerializedName("attributes")
    val attributes: List<String>

)