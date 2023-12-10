package com.example.attractions.di

import android.content.Context
import androidx.room.Room
import com.example.attractions.data.PhotoDataBase
import com.example.attractions.data.PhotoInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        PhotoDataBase::class.java,
        "photoData"
    ).build()

    @Provides
    @Singleton
    fun provideDao(db: PhotoDataBase) = db.photoDao()

    @Provides
    fun provideEntity() = PhotoInfo("", "")

}
