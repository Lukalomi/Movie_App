package com.example.midtermmovieapp

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.databinding.FragmentTopRatedDialogBinding

class TopRatedDialogFragment : Fragment() {

   private var binding: FragmentTopRatedDialogBinding? = null
    private val args: TopRatedDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopRatedDialogBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let {
            it.tvDialogMovieName.text = args.topRatedDialog?.title
            it.tvDialogMovieDesc.text = args.topRatedDialog?.overview
            Glide.with(requireContext()).load("https://image.tmdb.org/t/p/w500" + args.topRatedDialog?.posterPath).into(it.ivDialogMovie)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}