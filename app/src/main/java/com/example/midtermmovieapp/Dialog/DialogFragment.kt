package com.example.midtermmovieapp.Dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.databinding.FragmentDialogBinding

class DialogFragment : Fragment() {
    private var binding: FragmentDialogBinding? = null
    private val args: DialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fromDialogToHome()
        setDialog()

    }


    private fun setDialog() {

        binding?.let {
            it.tvDialogMovieDesc.text = args.singleDialog?.overview
            it.tvDialogMovieName.text = args.singleDialog?.title
            Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w500" + args.singleDialog?.posterPath)
                .into(it.ivDialogMovie)
        }
    }

    private fun fromDialogToHome() {
        binding!!.btnBackFromDialog.setOnClickListener {
            findNavController().navigate(DialogFragmentDirections.actionDialogFragmentToHomeFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}