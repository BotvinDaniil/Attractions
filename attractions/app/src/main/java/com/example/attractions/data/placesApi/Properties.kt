package com.example.attractions.data.placesApi


import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("dist")
    val dist: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("rate")
    val rate: Int,
    @SerializedName("xid")
    val xid: String,
)