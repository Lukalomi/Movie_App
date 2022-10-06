package com.example.midtermmovieapp.ui.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.midtermmovieapp.domain.models.UpcomingMoviesModel
import com.example.midtermmovieapp.data.remote.FetchedMovies
import javax.inject.Inject

class MovieUpcomingDataSource @Inject constructor(private val fetchedMovies: FetchedMovies) :
    PagingSource<Int, UpcomingMoviesModel.Result>() {
    override fun getRefreshKey(state: PagingState<Int, UpcomingMoviesModel.Result>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UpcomingMoviesModel.Result> {
        val page: Int = params.key ?: 1
        val prevPage = if (page != 1) page - 1 else null
        val nextPage = page + 1
        return try {
            val response = fetchedMovies.getUpcomingMoviesModel(page)
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
