package com.example.midtermmovieapp.ui.Dialog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.databinding.FragmentUpcomingDialogBinding
import com.example.midtermmovieapp.ui.favorites.FavoritesViewModel
import com.example.midtermmovieapp.data.local.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpcomingDialogFragment : Fragment() {


    private var binding: FragmentUpcomingDialogBinding? = null
    private val viewModelFav: FavoritesViewModel by viewModels()
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
        val movie = Movie(
            originalTitle = args.upComing?.originalTitle!!,
            posterPath = "https://image.tmdb.org/t/p/w500" + args.upComing?.posterPath,
            overview = args.upComing?.overview!!,
            voteAverage = args.upComing?.voteAverage!!,
            uid = 0,
            favoritesLogo = resources.getDrawable(R.mipmap.ic_launcher_round).toBitmap()

        )
        var count = 0

        binding!!.ivFavLogo.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

                if (count == 0) {
                    viewModelFav.insertMovie(movie)
                    binding!!.ivFavLogo.background = resources.getDrawable(R.drawable.ic_fav_logo)
                    Toast.makeText(
                        requireContext(),
                        "Movie ${movie.originalTitle} Has Been Added To Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    count++
                    Log.d("countadd", count.toString())
                } else {
                    binding!!.ivFavLogo.background =
                        resources.getDrawable(R.drawable.ic_fav_logo_uncheked)
                    Toast.makeText(
                        requireContext(),
                        "Movie ${movie.originalTitle} Has Been Removed From Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModelFav.readAllData().collect {
                        viewModelFav.deleteMovie(it[movie.uid])
                    }

                    count--
                    Log.d("countremove", count.toString())

                }
            }
        }
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