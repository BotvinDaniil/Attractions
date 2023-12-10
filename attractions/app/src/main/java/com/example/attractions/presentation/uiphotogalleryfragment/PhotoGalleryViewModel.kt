package com.example.attractions.presentation.uiphotogalleryfragment


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attractions.data.MainRepository
import com.example.attractions.data.PhotoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {
    fun getPhoto(): StateFlow<List<PhotoInfo>> {


        return repository.getPhotoList()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )
    }



}