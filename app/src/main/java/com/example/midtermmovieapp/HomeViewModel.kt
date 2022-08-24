package com.example.midtermmovieapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midtermmovieapp.Models.HomeModel
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
                val d = response.body()!!.results.forEach{
                    it.title
                }
            } else {
                _contentState.value = Resource.Error(response.errorBody()?.toString() ?: "")

            }
            _contentState.value = Resource.Loader(false)

        }
    }




}