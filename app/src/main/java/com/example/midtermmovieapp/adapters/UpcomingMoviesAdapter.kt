package com.example.midtermmovieapp.adapters

import com.example.midtermmovieapp.Models.UpcomingMoviesModel


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.Resource
import com.example.midtermmovieapp.databinding.SingleMovieItemBinding

class UpcomingMoviesAdapter(
    private val context: Context,
    var onClickListener: ((UpcomingMoviesModel.Result?) -> Unit)? = null
) : RecyclerView.Adapter<UpcomingMoviesAdapter.ItemViewHolder>() {

    var items2: Resource.Success<MutableList<UpcomingMoviesModel.Result?>> = Resource.Success(mutableListOf())

    fun submitList(
        newList:
        Resource.Success<MutableList<UpcomingMoviesModel.Result?>>
    ) {
        items2 = newList
    }

    inner class ItemViewHolder(val binding: SingleMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            SingleMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.tvMovieName.text = items2.data[position]?.title
        holder.binding.tvMovieName.text = items2.data[position]?.title
        holder.binding.tvMovieRating.text = items2.data[position]?.voteAverage.toString()

        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500" + items2.data[position]?.posterPath)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.ibMovieImage)

        holder.binding.ibMovieImage.setOnClickListener {
            onClickListener?.invoke(items2.data[position])
        }


    }

    override fun getItemCount() = items2.data.size


}