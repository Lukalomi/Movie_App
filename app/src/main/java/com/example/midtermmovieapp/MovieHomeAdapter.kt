package com.example.midtermmovieapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.databinding.SingleMovieItemBinding


class MovieHomeAdapter(
    var items: List<HomeModel>,
    private val context: Context
) : RecyclerView.Adapter<MovieHomeAdapter.ItemViewHolder>() {


    inner class ItemViewHolder(val binding: SingleMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            SingleMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.tvMovieName.text = items[position].originalTitle
        holder.binding.tvMovieName.text = items[position].voteAverage.toString()
        Glide.with(context).load(items[position].posterPath)
            .error(R.drawable.ic_launcher_background).into(holder.binding.ibMovieImage)
    }

    override fun getItemCount() = items.size


}