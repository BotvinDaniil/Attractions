package com.example.attractions.presentation.uimapfragment

sealed class State{

    object IsMoving :State()

    object IsStop: State()
}
