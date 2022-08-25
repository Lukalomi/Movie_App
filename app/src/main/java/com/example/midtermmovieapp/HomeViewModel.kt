package com.example.midtermmovieapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.Models.TopRatedMoviesModel
import com.example.midtermmovieapp.Models.UpcomingMoviesModel
import com.example.midtermmovieapp.network.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _contentState = MutableStateFlow<Resource<MutableList<HomeModel.Result>>>(Resource.Success(
        mutableListOf()))
    var contentState = _contentState.asStateFlow()

    fun getMovieContent() {
        viewModelScope.launch {
            _contentState.value = Resource.Loader(true)
            val response = RetrofitClient.FetchedMovies().getMovies()
            if (response.isSuccessful) {
                _contentState.value = Resource.Success(response.body()?.results ?: mutableListOf() )

            } else {
                _contentState.value = Resource.Error(response.errorBody()?.toString() ?: "")

            }
            _contentState.value = Resource.Loader(false)

        }
    }

    private val _contentTopRated = MutableStateFlow<Resource<MutableList<TopRatedMoviesModel.Result?>>>(Resource.Success(
        mutableListOf()))

    var contentTopRated = _contentTopRated.asStateFlow()

    fun getTopMovieContent() {
        viewModelScope.launch {
            _contentTopRated.value = Resource.Loader(true)
            val response = RetrofitClient.FetchedMovies().getTopRatedMovies()
            if (response.isSuccessful) {
                _contentTopRated.value = Resource.Success(response.body()?.results ?: mutableListOf() )

            } else {
                _contentTopRated.value = Resource.Error(response.errorBody()?.toString() ?: "")

            }
            _contentTopRated.value = Resource.Loader(false)

        }
    }

    private val _contentUpcoming = MutableStateFlow<Resource<MutableList<UpcomingMoviesModel.Result?>>>(Resource.Success(
        mutableListOf()))

    var contentUpcoming = _contentUpcoming.asStateFlow()


    fun getUpComingMovieContent() {
        viewModelScope.launch {
            _contentUpcoming.value = Resource.Loader(true)
            val response = RetrofitClient.FetchedMovies().getUpcomingMoviesModel()
            if (response.isSuccessful) {
                _contentUpcoming.value = Resource.Success(response.body()?.results ?: mutableListOf() )

            } else {
                _contentUpcoming.value = Resource.Error(response.errorBody()?.toString() ?: "")

            }
            _contentUpcoming.value = Resource.Loader(false)

        }
    }




}