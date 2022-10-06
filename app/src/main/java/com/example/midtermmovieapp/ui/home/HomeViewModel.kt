package com.example.midtermmovieapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.midtermmovieapp.data.remote.FetchedMovies
import com.example.midtermmovieapp.domain.models.HomeModel
import com.example.midtermmovieapp.utils.Resource
import com.example.midtermmovieapp.ui.dataSource.MovieDataSource
import com.example.midtermmovieapp.ui.dataSource.MovieTopDataSource
import com.example.midtermmovieapp.ui.dataSource.MovieUpcomingDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val fetchedMovies: FetchedMovies) : ViewModel() {


    val moviePager =
        Pager(config = PagingConfig(30), pagingSourceFactory = { MovieDataSource(fetchedMovies) }).flow.cachedIn(
            viewModelScope
        )


    val movieTopPager = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = { MovieTopDataSource(fetchedMovies) }).flow.cachedIn(viewModelScope)


    val movieUpcomingPager = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = { MovieUpcomingDataSource(fetchedMovies) }).flow.cachedIn(viewModelScope)



    private val _newState =
        MutableStateFlow<Resource<MutableList<HomeModel.Result>>>(Resource.Success(mutableListOf()))
    val newState = _newState.asStateFlow()

    fun getSearchedMovies(query: String) {
        viewModelScope.launch {

            val response = fetchedMovies.searchMovies(query)
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