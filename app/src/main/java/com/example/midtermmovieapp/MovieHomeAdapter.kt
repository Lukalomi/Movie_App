package com.example.midtermmovieapp

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.databinding.SingleMovieItemBinding


class MovieHomeAdapter(

    private val context: Context,
    var onClickListener: ((HomeModel.Result) -> Unit)? = null
) : RecyclerView.Adapter<MovieHomeAdapter.ItemViewHolder>() {
    var items: Resource.Success<MutableList<HomeModel.Result>> =
        Resource.Success<MutableList<HomeModel.Result>>()


    inner class ItemViewHolder(val binding: SingleMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            SingleMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.tvMovieName.text = items.data[position].title
        holder.binding.tvMovieRating.text = items.data[position].voteAverage.toString()

        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500" + items.data[position].posterPath)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.ibMovieImage)

        holder.binding.ibMovieImage.setOnClickListener {
            onClickListener?.invoke(items.data[position])
        }


    }

    override fun getItemCount() = items.data.size


}