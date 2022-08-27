package com.example.midtermmovieapp

import android.content.Loader
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.adapters.LoaderAdapter
import com.example.midtermmovieapp.adapters.MovieHomeAdapter
import com.example.midtermmovieapp.adapters.MovieTopRatedAdapter
import com.example.midtermmovieapp.adapters.UpcomingMoviesAdapter
import com.example.midtermmovieapp.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: MovieHomeAdapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapterTop: MovieTopRatedAdapter
    private lateinit var adapterUpcoming: UpcomingMoviesAdapter
    private val db = Firebase.firestore.collection("Movies")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding!!.appCompatImageButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
        }


        val customList = listOf(
            getString(R.string.most_popular), getString(R.string.top_rated), getString(
                R.string.upcoming
            )
        )
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            customList
        )
        binding!!.spinnerHome.adapter = spinnerAdapter
        binding!!.spinnerHome.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
//                    searchPopularPager()
                    getPopularMoviesPager()
                    binding!!.tvMovieListType.text = "Popular Movies"
                }
                if (position == 1) {
                    getTopMoviesPager()
                    binding!!.tvMovieListType.text = "Top Rated Movies"
                }

                if (position == 2) {
                    getUpcomingMoviesPager()
                    binding!!.tvMovieListType.text = "Upcoming Movies"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                getPopularMoviesPager()
            }

        }

    }


//    private fun searchPopularPager() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.moviePager.collect { response ->
//                    var displayList: MutableList<HomeModel.Result> = mutableListOf()
//
//
//                    binding!!.searchAction.setOnQueryTextListener(object :
//                        SearchView.OnQueryTextListener {
//                        override fun onQueryTextSubmit(query: String?): Boolean {
//                            return true
//                        }
//
//                        override fun onQueryTextChange(newText: String?): Boolean {
//                            if (newText!!.isNotEmpty()) {
//                                displayList.clear()
//                                val search = newText.lowercase(Locale.getDefault())
//                                response.map {
//                                    if (it.title.lowercase(Locale.getDefault())
//                                            .contains(search)
//                                    ) {
//                                        displayList.add(it)
//                                    }
//
//                                }
//
//
//                                if (displayList.size == 0) {
//                                    binding!!.tvNoMovie.visibility = View.VISIBLE
//                                    binding!!.tvNoMovie.setText("Movie $newText is not available")
//                                } else {
//                                    binding!!.tvNoMovie.visibility = View.GONE
//
//                                }
//
//                                adapter = MovieHomeAdapter(requireContext())
//                                viewLifecycleOwner.lifecycleScope.launch {
//                                    adapter.submitData(response)
//                                    binding!!.rvHomeRecycler.layoutManager =
//                                        GridLayoutManager(activity, 2)
//                                    binding!!.rvHomeRecycler.adapter = adapter
//                                    adapter.onClickListener = {
//                                        findNavController().navigate(
//                                            HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                                it,
//                                            )
//                                        )
//
//                                    }
//                                }
//
//
//                            } else {
//                                viewLifecycleOwner.lifecycleScope.launch {
//                                    adapter = MovieHomeAdapter(requireContext())
//                                    binding!!.rvHomeRecycler.layoutManager =
//                                        GridLayoutManager(activity, 2)
//                                    binding!!.rvHomeRecycler.adapter = adapter
//                                    adapter.onClickListener = { item ->
//                                        findNavController().navigate(
//                                            HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                                item,
//                                            )
//                                        )
//                                    }
//
//                                    binding!!.tvNoMovie.visibility = View.GONE
//                                    adapter.submitData(response)
//                                }
//
//
//                            }
//
//                            return true
//                        }
//
//                    })
//                }
//            }
//        }
//    }

//    private fun searchPopular() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.contentState.collect { response ->
//                    when (response) {
//                        is Resource.Success -> {
//                            var displayList = mutableListOf<HomeModel.Result>()
//
//                            binding!!.searchAction.setOnQueryTextListener(object :
//                                SearchView.OnQueryTextListener {
//                                override fun onQueryTextSubmit(query: String?): Boolean {
//                                    return true
//                                }
//
//                                override fun onQueryTextChange(newText: String?): Boolean {
//                                    if (newText!!.isNotEmpty()) {
//                                        displayList.clear()
//                                        val search = newText.lowercase(Locale.getDefault())
//                                        response.data.forEach {
//                                            if (it.title.lowercase(Locale.getDefault())
//                                                    .contains(search)
//                                            ) {
//                                                displayList.add(it)
//                                            }
//
//                                        }
//                                        if (displayList.size == 0) {
//                                            binding!!.tvNoMovie.visibility = View.VISIBLE
//                                            binding!!.tvNoMovie.setText("Movie $newText is not available")
//                                        } else {
//                                            binding!!.tvNoMovie.visibility = View.GONE
//
//                                        }
//
//                                        adapter = MovieHomeAdapter(requireContext())
//                                        adapter.submitList(Resource.Success(displayList))
//                                        binding!!.rvHomeRecycler.layoutManager =
//                                            GridLayoutManager(activity, 2)
//                                        binding!!.rvHomeRecycler.adapter = adapter
//                                        adapter.onClickListener = {
//                                            findNavController().navigate(
//                                                HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                                    it,
//                                                )
//                                            )
//
//                                        }
//
//                                    } else {
//                                        adapter = MovieHomeAdapter(requireContext())
//                                        binding!!.rvHomeRecycler.layoutManager =
//                                            GridLayoutManager(activity, 2)
//                                        binding!!.rvHomeRecycler.adapter = adapter
//                                        adapter.onClickListener = { item ->
//                                            findNavController().navigate(
//                                                HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                                    item,
//                                                )
//                                            )
//                                        }
//
//                                        binding!!.tvNoMovie.visibility = View.GONE
//                                        adapter.submitList(Resource.Success(response.data))
//
//
//                                    }
//
//                                    return true
//                                }
//
//                            })
//                        }
//                        is Resource.Error -> {
//                            Toast.makeText(requireContext(), response.errorMsg, Toast.LENGTH_SHORT)
//                                .show()
//
//                        }
//                        is Resource.Loader -> {
//                            if (response.isLoading != binding!!.pbHome.isVisible) {
//                                binding!!.pbHome.visibility = View.GONE
//                                binding!!.tvHomeLoader.visibility = View.GONE
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    }


    private fun getPopularMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding!!.pbHome.visibility = View.GONE
                binding!!.tvHomeLoader.visibility = View.GONE
                viewModel.moviePager.collect {
                    adapter = MovieHomeAdapter(requireContext())
                    binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                    binding!!.rvHomeRecycler.adapter = adapter
                    adapter.withLoadStateFooter(footer = LoaderAdapter())
                    adapter.submitData(it)

                    adapter.onClickListener = { item ->
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                                item,
                            )
                        )

                    }
                }
            }
        }
    }

    private fun getTopMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding!!.pbHome.visibility = View.GONE
                binding!!.tvHomeLoader.visibility = View.GONE
                viewModel.movieTopPager.collect {
                    adapterTop = MovieTopRatedAdapter(requireContext())
                    binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                    binding!!.rvHomeRecycler.adapter = adapterTop
                    adapterTop.withLoadStateFooter(footer = LoaderAdapter())
                    adapterTop.submitData(it)

//                    adapterTop.onClickListener = { item ->
//                        findNavController().navigate(
//                            HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                item,
//                            )
//                        )
//
//                    }
                }
            }
        }
    }

    private fun getUpcomingMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding!!.pbHome.visibility = View.GONE
                binding!!.tvHomeLoader.visibility = View.GONE
                viewModel.movieUpcomingPager.collect {
                    adapterUpcoming = UpcomingMoviesAdapter(requireContext())
                    binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                    binding!!.rvHomeRecycler.adapter = adapterUpcoming
                    adapterUpcoming.withLoadStateFooter(footer = LoaderAdapter())
                    adapterUpcoming.submitData(it)

//                    adapterTop.onClickListener = { item ->
//                        findNavController().navigate(
//                            HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                item,
//                            )
//                        )
//
//                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}