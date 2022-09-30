package com.example.midtermmovieapp.ui.Dialog

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.databinding.FragmentDialogBinding
import com.example.midtermmovieapp.ui.favorites.FavoritesViewModel
import com.example.midtermmovieapp.data.local.Movie
import com.example.midtermmovieapp.utils.Converters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DialogFragment : Fragment() {
    private var binding: FragmentDialogBinding? = null
    private val args: DialogFragmentArgs by navArgs()
    private val viewModelFav: FavoritesViewModel by viewModels()


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
//        val movie = Movie(
//            originalTitle = args.singleDialog?.originalTitle!!,
//            posterPath = "https://image.tmdb.org/t/p/w500" + args.singleDialog?.posterPath,
//            overview = args.singleDialog?.overview!!,
//            voteAverage = args.singleDialog?.voteAverage!!,
//            uid = 0,
//            favoritesLogo = null
//        )
         var count = 0
        var movie = Movie(
            originalTitle = args.singleDialog?.originalTitle!!,
            posterPath = "https://image.tmdb.org/t/p/w500" + args.singleDialog?.posterPath,
            overview = args.singleDialog?.overview!!,
            voteAverage = args.singleDialog?.voteAverage!!,
            uid = 0,
            favoritesLogo = resources.getDrawable(R.mipmap.ic_launcher_round).toBitmap()
        )
        binding!!.ivFavLogo.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

            if(count == 0) {

                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton("Yes") { _, _ ->
                    viewModelFav.insertMovie(movie)
                    binding!!.ivFavLogo.background = resources.getDrawable(R.drawable.ic_fav_logo)
                    Toast.makeText(
                        requireContext(),
                        "Movie ${movie.originalTitle} Has Been Added To Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    count++
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.setTitle("Add ${movie.originalTitle}?")
                builder.setMessage("Are You Sure You Want To Add ${movie.originalTitle} To Favorite Movies?")
                builder.create().show()

            }
            else{
                binding!!.ivFavLogo.background =
                    resources.getDrawable(R.drawable.ic_fav_logo_uncheked)
                Toast.makeText(
                    requireContext(),
                    "Movie ${movie.originalTitle} Has Been Removed From Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                viewModelFav.readAllData().collect{
                    viewModelFav.deleteMovie(it[movie.uid])
                }

                count--
                Log.d("countremove",count.toString())

            }
        }
        }
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