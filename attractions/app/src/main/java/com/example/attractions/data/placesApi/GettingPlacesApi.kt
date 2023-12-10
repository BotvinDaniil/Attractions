package com.example.attractions.data.placesApi

import com.example.attractions.data.placesApi.InfoDTO.PlaceInfoDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val URL = "https://api.opentripmap.com/0.1/"

object GettingPlacesApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gettingPlacesApi: GettingPlacesFromRadiusAPi =
        retrofit.create(GettingPlacesFromRadiusAPi::class.java)


}

interface GettingPlacesFromRadiusAPi {
    @GET("ru/places/radius?radius=300&apikey=5ae2e3f221c38a28845f05b6ae35489ee72cafd96c056ae3ccc6f492")
    fun getInterestingPlaces(@Query("lon") longitude : Double =30.324298, @Query("lat") latitude: Double= 59.935881): Call<InterrestingPlacesDto>

    @GET("ru/places/xid/{xid}")
    fun getInterestingPlaceInfo(
        @Path("xid") id: String,
        @Query("apikey") apiKey: String = "5ae2e3f221c38a28845f05b6ae35489ee72cafd96c056ae3ccc6f492"
    ): Call<PlaceInfoDTO>
}