package com.example.midtermmovieapp.di

import android.app.Application
import androidx.room.Room
import com.example.midtermmovieapp.data.local.MovieLocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDBModule {


    @Provides
    @Singleton
    fun provideRoomDB(app: Application) =
        Room.databaseBuilder(
            app,
            MovieLocalDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().build()


    @Provides
    @Singleton
    fun provideUserDao(db: MovieLocalDatabase) = db.movieDao()


}