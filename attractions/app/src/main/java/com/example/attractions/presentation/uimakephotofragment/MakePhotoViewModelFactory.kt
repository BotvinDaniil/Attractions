package com.example.attractions.presentation.uimakephotofragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.attractions.data.MainRepository
import com.example.attractions.data.PhotoDataBase
import javax.inject.Inject

class MakePhotoViewModelFactory @Inject constructor(val database: PhotoDataBase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MakePhotoFragmentViewModel::class.java)){

            return MakePhotoFragmentViewModel (MainRepository(database.photoDao()) ) as T
        }
        throw IllegalArgumentException( "Unknown class")
    }
}
