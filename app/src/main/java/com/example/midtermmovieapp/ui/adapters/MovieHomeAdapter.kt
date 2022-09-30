package com.example.midtermmovieapp.ui.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.domain.models.HomeModel
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.databinding.SingleMovieItemBinding


class MovieHomeAdapter(
    private val context: Context,
    var onClickListener: ((HomeModel.Result) -> Unit)? = null,
) : PagingDataAdapter<HomeModel.Result,MovieHomeAdapter.ItemViewHolder>(diffUtilCallback()) {



    inner class ItemViewHolder(val binding: SingleMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var model: HomeModel.Result? = null
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
                    getItem(position = bindingAdapterPosition)?.let { it1 ->
                        onClickListener?.invoke(
                            it1
                        )
                    }
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

    class diffUtilCallback():DiffUtil.ItemCallback<HomeModel.Result>() {
        override fun areItemsTheSame(
            oldItem: HomeModel.Result,
            newItem: HomeModel.Result
        ): Boolean {
          return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: HomeModel.Result,
            newItem: HomeModel.Result
        ): Boolean {
            return oldItem == newItem
        }


    }


}