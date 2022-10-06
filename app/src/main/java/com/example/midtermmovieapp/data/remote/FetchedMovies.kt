package com.example.midtermmovieapp.data.remote

import com.example.midtermmovieapp.domain.models.HomeModel
import com.example.midtermmovieapp.domain.models.TopRatedMoviesModel
import com.example.midtermmovieapp.domain.models.UpcomingMoviesModel
import com.example.midtermmovieapp.utils.ApiEndpoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface FetchedMovies {
    @GET(ApiEndpoints.searchMovies)
    suspend fun searchMovies (@Query("query") query: String): Response<HomeModel>

    @GET(ApiEndpoints.getMovies)
    suspend fun getMovies(@Query("page")page:Int): Response<HomeModel>

    @GET(ApiEndpoints.getTopRatedMovies)
    suspend fun getTopRatedMovies(@Query("page")page:Int): Response<TopRatedMoviesModel>

    @GET(ApiEndpoints.getUpcomingMoviesModel)
    suspend fun getUpcomingMoviesModel(@Query("page")page:Int):Response<UpcomingMoviesModel>

}

