package com.example.midtermmovieapp.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.midtermmovieapp.Models.HomeModel

class DiffUtil(
    val movieList: MutableList<HomeModel.Result?>,
    val searchedMovieList: MutableList<HomeModel.Result?>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return movieList.size
    }

    override fun getNewListSize(): Int {
        return searchedMovieList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return movieList[oldItemPosition]?.id == searchedMovieList[newItemPosition]?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            movieList[oldItemPosition]?.id != searchedMovieList[newItemPosition]?.id -> {
                false
            }

            else -> true
        }
    }
}