package com.example.midtermmovieapp.adapters

import com.example.midtermmovieapp.Models.UpcomingMoviesModel


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.Models.TopRatedMoviesModel
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.Resource
import com.example.midtermmovieapp.databinding.SingleMovieItemBinding

class UpcomingMoviesAdapter(
    private val context: Context,
    var onClickListener: ((UpcomingMoviesModel.Result?) -> Unit)? = null,
    var onFavClickListener: ((UpcomingMoviesModel.Result?) -> Unit)? = null

) : PagingDataAdapter<UpcomingMoviesModel.Result,UpcomingMoviesAdapter.ItemViewHolder>(diffUtilCallback()) {



    inner class ItemViewHolder(val binding: SingleMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var model: UpcomingMoviesModel.Result? = null
        fun onBind() {
            model = getItem(bindingAdapterPosition)
            binding.apply {
                tvMovieName.text = model?.title
                tvMovieRating.text = model?.voteAverage.toString()
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" +  model?.posterPath)
                    .error(R.drawable.ic_launcher_background)
                    .into(ibMovieImage)
                ibMovieImage.setOnClickListener {
                    onClickListener?.invoke(model!!)
                }
                ivFavLogo.setOnClickListener {
                    onFavClickListener?.invoke(model!!)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            SingleMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind()



    }
    class diffUtilCallback(): DiffUtil.ItemCallback<UpcomingMoviesModel.Result>() {
        override fun areItemsTheSame(
            oldItem: UpcomingMoviesModel.Result,
            newItem: UpcomingMoviesModel.Result
        ): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UpcomingMoviesModel.Result,
            newItem: UpcomingMoviesModel.Result
        ): Boolean {
            return oldItem == newItem
        }


    }
}