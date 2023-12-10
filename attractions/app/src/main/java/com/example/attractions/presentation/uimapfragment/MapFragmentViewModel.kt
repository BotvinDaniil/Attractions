package com.example.attractions.presentation.uimapfragment


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attractions.data.MainRepository
import com.example.attractions.data.placesApi.Feature
import com.example.attractions.data.placesApi.InfoDTO.PlaceInfoDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapFragmentViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.IsMoving)
    val state =_state.asStateFlow()

    fun mapIsStopped(isStop: Boolean) {
         if(!isStop)
            _state.value = State.IsMoving
        else _state.value= State.IsStop

    }


    suspend fun getPlaces(lon:Double,lat:Double):List<Feature>{
        var places = listOf<Feature>()
        viewModelScope.launch {
            places=repository.getPlaces(lon = lon, lat = lat)!!.features
        }.join()
    return places }

    suspend fun getPlaceInfo(xid: String):PlaceInfoDTO {
        lateinit var info: PlaceInfoDTO
        viewModelScope.launch {
            info=repository.getPlaceInfo(xid)
        }.join()
               return  info
    }

}