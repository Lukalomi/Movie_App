package com.example.midtermmovieapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.utils.Resource
import com.example.midtermmovieapp.databinding.SingleMovieItemBinding

class HomeNormalAdapter(val context: Context) : RecyclerView.Adapter<HomeNormalAdapter.SearchViewHolder>() {
    var onClickListener: ((HomeModel.Result) -> Unit)? = null



    var list: Resource.Success<MutableList<HomeModel.Result>> = Resource.Success(mutableListOf())
    fun submitList(newList: Resource.Success<MutableList<HomeModel.Result>>) {
        list = newList
    }

    inner class SearchViewHolder(val binding: SingleMovieItemBinding):RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SingleMovieItemBinding.inflate(layoutInflater,parent,false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.binding.tvMovieName.text = list.data[position].title
        holder.binding.tvMovieRating.text = list.data[position].voteAverage.toString()
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500" +  list.data[position].posterPath)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.ibMovieImage)
        holder.binding.ibMovieImage.setOnClickListener{
            onClickListener?.invoke(list.data[position])
        }
    }

    override fun getItemCount(): Int {
       return list.data.size
    }


}