package com.example.midtermmovieapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.midtermmovieapp.domain.models.HomeModel
import com.example.midtermmovieapp.utils.Resource
import com.example.midtermmovieapp.ui.dataSource.MovieDataSource
import com.example.midtermmovieapp.ui.dataSource.MovieTopDataSource
import com.example.midtermmovieapp.ui.dataSource.MovieUpcomingDataSource
import com.example.midtermmovieapp.data.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {


    val moviePager =
        Pager(config = PagingConfig(30), pagingSourceFactory = { MovieDataSource() }).flow.cachedIn(
            viewModelScope
        )


    val movieTopPager = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = { MovieTopDataSource() }).flow.cachedIn(viewModelScope)


    val movieUpcomingPager = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = { MovieUpcomingDataSource() }).flow.cachedIn(viewModelScope)



    private val _newState =
        MutableStateFlow<Resource<MutableList<HomeModel.Result>>>(Resource.Success(mutableListOf()))
    val newState = _newState.asStateFlow()

    fun getSearchedMovies(query: String) {
        viewModelScope.launch {
            val response = RetrofitClient.FetchedMovies().searchMovies(query)
            if (response.isSuccessful) {
                _newState.value = Resource.Success(response.body()?.results ?: mutableListOf())
            } else {
                val errorBody = response.errorBody()
                _newState.value = Resource.Error(errorBody?.toString() ?: "")
            }
            _newState.value = Resource.Loader(false)
        }
    }
}