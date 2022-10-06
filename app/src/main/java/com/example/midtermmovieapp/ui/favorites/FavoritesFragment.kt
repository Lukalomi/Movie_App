package com.example.midtermmovieapp.ui.favorites

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.ui.adapters.FavoritesAdapter
import com.example.midtermmovieapp.databinding.FragmentFavoritesBinding
import com.example.midtermmovieapp.data.local.Movie
import com.example.midtermmovieapp.domain.models.HomeModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var binding: FragmentFavoritesBinding? = null
    private val viewModel: FavoritesViewModel by activityViewModels()
    private lateinit var favoritesAdapter: FavoritesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayFavMovies()
        goToHomeFragment()
        clearFavList()
        searchFavMovies()

    }

    private fun displayFavMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.readAllData().collect { list ->

                setAdapter()
                favoritesAdapter.submitList(list)
                if (list.size > 0) {
                    binding!!.btnClearFav.visibility = View.VISIBLE

                } else {
                    binding!!.btnClearFav.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "You Don't Have Any Favorite Movies, Please Add From The Main Pages",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }


    private fun setAdapter() {

        favoritesAdapter = FavoritesAdapter(requireContext())
        binding!!.rvFavRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding!!.rvFavRecycler.adapter = favoritesAdapter
        favoritesAdapter.onClickListener = {

            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton("Yes") { _, _ ->
                viewModel.deleteMovie(it)
                displayFavMovies()
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.readAllData().collect {
                        favoritesAdapter.submitList(it)
                    }
                }
                Toast.makeText(
                    requireContext(),
                    "Movie ${it.originalTitle} has been deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.setTitle("Delete ${it.originalTitle}?")
            builder.setMessage("Are You Sure You Want To Delete ${it.originalTitle}")
            builder.create().show()

        }

    }

    private fun goToHomeFragment() {
        binding!!.tvAppName.setOnClickListener {
            findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToHomeFragment())
        }
    }


    private fun clearFavList() {
        binding!!.btnClearFav.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteAllData()
                setAdapter()
                favoritesAdapter.submitList(emptyList())
                binding!!.btnClearFav.visibility = View.GONE
            }
        }


    }

    private fun hideKeyboard(): Boolean {
        val inputManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            activity!!.currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        return true
    }


    private fun searchFavMovies() {
        var displayList: MutableList<Movie> = mutableListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.readAllData().collect() {
                binding!!.searchAction.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        hideKeyboard()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText!!.isNotEmpty()) {
                            displayList.clear()
                            var search = newText!!.lowercase(Locale.getDefault())
                            it.forEach {
                                if (it.originalTitle.lowercase(Locale.getDefault())
                                        .contains(search)
                                ) {
                                    displayList.add(it)

                                }
                            }

                            setAdapter()
                            favoritesAdapter.submitList(displayList)
                        } else {
                            displayFavMovies()
                            hideKeyboard()
                            binding!!.searchAction.clearFocus()
                        }
                        return true
                    }

                }

                )
            }

        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}