package com.example.midtermmovieapp.di

import android.app.Application
import androidx.room.Room
import com.example.midtermmovieapp.data.local.MovieLocalDatabase
import com.example.midtermmovieapp.data.remote.FetchedMovies
import com.example.midtermmovieapp.utils.ApiEndpoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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


    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .build()

    @Singleton
    @Provides
    fun apiService(): FetchedMovies =
        Retrofit.Builder().baseUrl(ApiEndpoints.BASE_URL).client(providesOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(FetchedMovies::class.java)


}


