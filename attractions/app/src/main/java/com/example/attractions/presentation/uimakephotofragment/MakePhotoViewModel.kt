package com.example.attractions.presentation.uimakephotofragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attractions.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class MakePhotoFragmentViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {

    fun savePhoto(uri: String, name:String) {

        viewModelScope.launch {
                        repository.savePhotoInfo(uri, name)
        }

           }
}



