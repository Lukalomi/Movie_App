package com.example.midtermmovieapp.network

import com.example.midtermmovieapp.Models.HomeModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    val retrofitBuilder by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun FetchedMovies() = retrofitBuilder.create(FetchedMovies::class.java)
}


interface FetchedMovies {
    @GET("movie/550?api_key=7ed909b5505ef24375a120e6e2c06512")
    suspend fun getMovies(): Response<HomeModel>
}