package com.example.midtermmovieapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midtermmovieapp.databinding.LoaderBinding

class LoaderAdapter:LoadStateAdapter<LoaderAdapter.LoaderViewHolder> () {
    class LoaderViewHolder (val binding:LoaderBinding):RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState):
            LoaderViewHolder {
        return LoaderViewHolder(LoaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.binding.root.isVisible = loadState is LoadState.Loading

    }




}