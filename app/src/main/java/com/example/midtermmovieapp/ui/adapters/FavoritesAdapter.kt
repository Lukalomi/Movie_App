package com.example.midtermmovieapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.databinding.SingleMovieItemFavoritesBinding
import com.example.midtermmovieapp.data.local.Movie

class FavoritesAdapter(val context:Context
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>()
{
    var onClickListener: ((Movie) -> Unit)? = null

    var list: List<Movie> = mutableListOf()
    fun submitList(newList: List<Movie>) {
        list = newList
    }

    inner class FavoritesViewHolder(val binding: SingleMovieItemFavoritesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SingleMovieItemFavoritesBinding.inflate(layoutInflater, parent, false)
        return FavoritesViewHolder(binding)

    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500" +  list[position].posterPath)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.ibMovieImage)
        holder.binding.tvMovieName.text = list[position].originalTitle
        holder.binding.tvMovieRating.text = list[position].voteAverage.toString()


        holder.binding.ivFavLogo.setOnClickListener{
            onClickListener?.invoke(list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.size

    }
}