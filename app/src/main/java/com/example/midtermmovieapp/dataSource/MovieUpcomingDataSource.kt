package com.example.midtermmovieapp.dataSource

import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.midtermmovieapp.Models.UpcomingMoviesModel
import com.example.midtermmovieapp.network.RetrofitClient

class MovieUpcomingDataSource : PagingSource<Int, UpcomingMoviesModel.Result>() {
    override fun getRefreshKey(state: PagingState<Int, UpcomingMoviesModel.Result>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UpcomingMoviesModel.Result> {
        val page: Int = params.key ?: 1
        val prevPage = if (page != 1) page - 1 else null
        val nextPage = page + 1
        return try {
            val response = RetrofitClient.FetchedMovies().getUpcomingMoviesModel(page)
            if (response.isSuccessful) {
                val movies = response.body()?.results ?: mutableListOf()
                return LoadResult.Page(movies, prevPage, nextPage)
            } else {
                return LoadResult.Error(Throwable())
            }
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}
