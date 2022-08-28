package com.example.midtermmovieapp.Dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.databinding.FragmentUpcomingDialogBinding

class UpcomingDialogFragment : Fragment() {


    private var binding: FragmentUpcomingDialogBinding? = null

    private val args: UpcomingDialogFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpcomingDialogBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialog()
        fromDialogToHome()


    }

    private fun setDialog() {
        binding?.let {
            it.tvDialogMovieDesc.text = args.upComing?.overview
            it.tvDialogMovieName.text = args.upComing?.title
            Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w500" + args.upComing?.posterPath)
                .into(it.ivDialogMovie)
        }
    }

    private fun fromDialogToHome() {
        binding!!.btnBackFromDialog.setOnClickListener {
            findNavController().navigate(UpcomingDialogFragmentDirections.actionUpcomingDialogFragmentToHomeFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}