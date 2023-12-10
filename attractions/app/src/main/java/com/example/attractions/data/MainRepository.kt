package com.example.attractions.data

import com.example.attractions.data.placesApi.GettingPlacesApi
import com.example.attractions.data.placesApi.InfoDTO.PlaceInfoDTO
import com.example.attractions.data.placesApi.InterrestingPlacesDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.concurrent.thread


class MainRepository @Inject constructor(val dao: PhotoDAO) {

    lateinit var response: retrofit2.Response<InterrestingPlacesDto>
    lateinit var responseInfo: retrofit2.Response<PlaceInfoDTO>

    suspend fun savePhotoInfo(uri: String, name: String) {

        withContext(Dispatchers.IO)
        {
            dao.addPhoto(
                PhotoInfo(
                    photoUri = uri,
                    photoDate = name
                )
            )
        }
    }

    fun getPhotoList(): Flow<List<PhotoInfo>> {
        return dao.getAllPhoto()
    }

    suspend fun getPlaces(lon: Double, lat: Double): InterrestingPlacesDto? {

        withContext(Dispatchers.IO) {
            thread {
                response = GettingPlacesApi.gettingPlacesApi.getInterestingPlaces(
                    longitude = lon,
                    latitude = lat
                ).execute()
            }.join()
        }

        return response.body()
    }

    suspend fun getPlaceInfo(xid: String): PlaceInfoDTO {
        withContext(Dispatchers.IO) {
            thread {
                responseInfo =
                    GettingPlacesApi.gettingPlacesApi.getInterestingPlaceInfo(xid).execute()
            }.join()
        }
        return responseInfo.body()!!
    }

}