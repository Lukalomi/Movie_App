package com.example.midtermmovieapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.Models.TopRatedMoviesModel
import com.example.midtermmovieapp.Models.UpcomingMoviesModel
import com.example.midtermmovieapp.dataSource.MovieDataSource
import com.example.midtermmovieapp.dataSource.MovieTopDataSource
import com.example.midtermmovieapp.dataSource.MovieUpcomingDataSource
import com.example.midtermmovieapp.network.RetrofitClient
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
}