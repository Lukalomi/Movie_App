package com.example.midtermmovieapp.ui.favorites

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.midtermmovieapp.ui.adapters.FavoritesAdapter
import com.example.midtermmovieapp.databinding.FragmentFavoritesBinding
import com.example.midtermmovieapp.data.local.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var binding: FragmentFavoritesBinding? = null
    private val viewModel: FavoritesViewModel by viewModels()
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
            viewModel.readAllData().collect() {
                setAdapter()
                favoritesAdapter.submitList(it)
                if (it.size > 0) {
                    binding!!.btnClearFav.visibility = View.VISIBLE

                } else {
                    binding!!.btnClearFav.visibility = View.GONE

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
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.readAllData().collect {
                    favoritesAdapter.submitList(it)
                }
            }
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

    private fun searchFavMovies() {
        var displayList: MutableList<Movie> = mutableListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.readAllData().collect() {
                binding!!.searchAction.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
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