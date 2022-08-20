package com.example.midtermmovieapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _contentState = MutableStateFlow<List<HomeModel.Result>>(emptyList())
    var contentState:StateFlow<List<HomeModel.Result>> = _contentState

    fun getMovieContent() {
        viewModelScope.launch {
            val response = RetrofitClient.FetchedMovies().getMovies()
            if(response.isSuccessful) {
                _contentState.value = response.body()?.results ?: emptyList()
                Log.d("serverresponse",response.body().toString())
            }
            else {
                Log.d("fetchedError",response.errorBody().toString())
            }
        }
    }
}