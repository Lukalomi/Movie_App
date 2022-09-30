package com.example.midtermmovieapp.ui.Dialog

import android.os.Bundle
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
import com.example.midtermmovieapp.databinding.FragmentTopRatedDialogBinding
import com.example.midtermmovieapp.ui.favorites.FavoritesViewModel
import com.example.midtermmovieapp.data.local.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TopRatedDialogFragment : Fragment() {

    private var binding: FragmentTopRatedDialogBinding? = null
    private val args: TopRatedDialogFragmentArgs by navArgs()
    private val viewModelFav: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopRatedDialogBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialog()
        fromDialogToHome()

        val movie = Movie(
            originalTitle = args.topRatedDialog?.originalTitle!!,
            posterPath = "https://image.tmdb.org/t/p/w500" + args.topRatedDialog?.posterPath,
            overview = args.topRatedDialog?.overview!!,
            voteAverage = args.topRatedDialog?.voteAverage!!,
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

                }
            }
        }

    }


    private fun setDialog() {
        binding?.let {
            it.tvDialogMovieName.text = args.topRatedDialog?.title
            it.tvDialogMovieDesc.text = args.topRatedDialog?.overview
            Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w500" + args.topRatedDialog?.posterPath)
                .into(it.ivDialogMovie)
        }
    }


    private fun fromDialogToHome() {

        binding!!.btnBackFromDialog.setOnClickListener {
            findNavController().navigate(TopRatedDialogFragmentDirections.actionTopRatedDialogFragmentToHomeFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}