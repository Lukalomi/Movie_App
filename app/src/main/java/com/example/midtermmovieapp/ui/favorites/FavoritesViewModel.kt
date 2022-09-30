package com.example.midtermmovieapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midtermmovieapp.data.local.Movie
import com.example.midtermmovieapp.data.local.MovieDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val movieDao: MovieDao) : ViewModel() {


     fun readAllData() = flow {
        emit(movieDao.getAll())
    }

    fun insertMovie(movie:Movie) {
        viewModelScope.launch {
            movieDao.addMovie(movie)
        }
    }

    suspend fun deleteAllData() = movieDao.deleteAll()


    fun deleteMovie(movie:Movie) {
        viewModelScope.launch {
            movieDao.delete(movie)
        }
    }
}