package com.example.midtermmovieapp.Dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import com.bumptech.glide.Glide
import com.example.midtermmovieapp.*

import com.example.midtermmovieapp.databinding.FragmentDialogBinding

import kotlinx.coroutines.launch

class DialogFragment : Fragment() {
    private var binding: FragmentDialogBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMovieContent()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.contentState.collect {
                    val listSize = it.size
                    var i = 0
                    while (i < listSize) {
                        binding!!.tvDialogMovieName.text = it[i].title
                        binding!!.tvDialogMovieDesc.text = it[i].overview
                        Glide.with(requireContext())
                            .load("https://image.tmdb.org/t/p/w500" + it[i].posterPath)
                            .error(R.drawable.ic_launcher_background)
                            .into(binding!!.ivDialogMovie)
                        i++
                    }
                }

            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}