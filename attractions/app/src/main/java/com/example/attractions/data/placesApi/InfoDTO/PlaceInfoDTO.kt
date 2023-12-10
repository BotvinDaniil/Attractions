package com.example.attractions.data.placesApi.InfoDTO


import com.google.gson.annotations.SerializedName

data class PlaceInfoDTO(
    @SerializedName("address")
    val address: Address,
    @SerializedName("kinds")
    val kinds: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("point")
    val point: Point,
    @SerializedName("rate")
    val rate: String,
    @SerializedName("sources")
    val sources: Sources,

)