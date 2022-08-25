package com.example.midtermmovieapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.Models.TopRatedMoviesModel
import com.example.midtermmovieapp.Models.UpcomingMoviesModel
import com.example.midtermmovieapp.adapters.MovieHomeAdapter
import com.example.midtermmovieapp.adapters.MovieTopRatedAdapter
import com.example.midtermmovieapp.adapters.UpcomingMoviesAdapter
import com.example.midtermmovieapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: MovieHomeAdapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapterTop: MovieTopRatedAdapter
    private lateinit var adapterUpcoming: UpcomingMoviesAdapter

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



        val customList = listOf("Most Popular", "Top Rated", "Upcoming Movies")
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
                getPopularMovies()
                if (position == 0) {
                    getPopularMovies()
                    searchPopular()
                    binding!!.tvMovieListType.text = "Popular Movies"
                }
                if(position == 1) {
                    getTopRatedMovies()
                    binding!!.tvMovieListType.text = "Top Rated Movies"
                    searchTop()

                }
                if(position == 2) {
                    getUpcomingMovies()
                    binding!!.tvMovieListType.text = "Upcoming Movies"
                    searchUpcoming()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                    getUpcomingMovies()
            }

        }

    }


    private fun searchPopular() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.contentState.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        var displayList = mutableListOf<HomeModel.Result>()

                        binding!!.searchAction.setOnQueryTextListener(object :
                            SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                if (newText!!.isNotEmpty()) {
                                    displayList.clear()
                                    val search = newText.lowercase(Locale.getDefault())
                                    response.data.forEach {
                                        if (it.title.lowercase(Locale.getDefault())
                                                .contains(search)
                                        ) {
                                            displayList.add(it)
                                        }

                                    }
                                    if (displayList.size == 0) {
                                        binding!!.tvNoMovie.visibility = View.VISIBLE
                                        binding!!.tvNoMovie.setText("Movie $newText is not available")
                                    } else {
                                        binding!!.tvNoMovie.visibility = View.GONE

                                    }

                                    adapter = MovieHomeAdapter(requireContext())
                                    adapter.submitList(Resource.Success(displayList))
                                    binding!!.rvHomeRecycler.layoutManager =
                                        GridLayoutManager(activity, 2)
                                    binding!!.rvHomeRecycler.adapter = adapter
                                    adapter.onClickListener = {
                                        findNavController().navigate(
                                            HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                                                it,
                                            )
                                        )
                                    }

                                } else {
                                    adapter = MovieHomeAdapter(requireContext())
                                    binding!!.rvHomeRecycler.layoutManager =
                                        GridLayoutManager(activity, 2)
                                    binding!!.rvHomeRecycler.adapter = adapter
                                    adapter.onClickListener = { item ->
                                        findNavController().navigate(
                                            HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                                                item,
                                            )
                                        )
                                    }

                                    binding!!.tvNoMovie.visibility = View.GONE
                                    adapter.submitList(Resource.Success(response.data))


                                }

                                return true
                            }

                        })
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), response.errorMsg, Toast.LENGTH_SHORT)
                            .show()

                    }
                    is Resource.Loader -> {
                        if (response.isLoading != binding!!.pbHome.isVisible) {
                            binding!!.pbHome.visibility = View.GONE
                            binding!!.tvHomeLoader.visibility = View.GONE

                        }
                    }
                }
            }
        }
        }

    }

    private fun searchTop() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contentTopRated.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            var displayList = mutableListOf<TopRatedMoviesModel.Result?>()

                            binding!!.searchAction.setOnQueryTextListener(object :
                                SearchView.OnQueryTextListener {
                                override fun onQueryTextSubmit(query: String?): Boolean {
                                    return true
                                }

                                override fun onQueryTextChange(newText: String?): Boolean {
                                    if (newText!!.isNotEmpty()) {
                                        displayList.clear()
                                        val search = newText.lowercase(Locale.getDefault())
                                        response.data.forEach {
                                            if (it?.title?.lowercase(Locale.getDefault())
                                                    ?.contains(search) == true
                                            ) {
                                                displayList.add(it)
                                            }

                                        }
                                        if (displayList.size == 0) {
                                            binding!!.tvNoMovie.visibility = View.VISIBLE
                                            binding!!.tvNoMovie.setText("Movie $newText is not available")
                                        } else {
                                            binding!!.tvNoMovie.visibility = View.GONE

                                        }

                                        adapterTop = MovieTopRatedAdapter(requireContext())
                                        adapterTop.submitList(Resource.Success(displayList))
                                        binding!!.rvHomeRecycler.layoutManager =
                                            GridLayoutManager(activity, 2)
                                        binding!!.rvHomeRecycler.adapter = adapterTop
//                                        adapterTop.onClickListener = { item ->
//                                            findNavController().navigate(
//                                                HomeFragmentDirections.actionHomeFragmentToDialogTopFragment(
//                                                    item,
//                                                )
//                                            )
//                                        }

                                    } else {
                                        adapterTop = MovieTopRatedAdapter(requireContext())
                                        binding!!.rvHomeRecycler.layoutManager =
                                            GridLayoutManager(activity, 2)
                                        binding!!.rvHomeRecycler.adapter = adapterTop
//                                        adapterTop.onClickListener = {
//                                            findNavController().navigate(
//                                                HomeFragmentDirections.actionHomeFragmentToDialogTopFragment(
//                                                    it,
//                                                )
//                                            )
//                                        }

                                        binding!!.tvNoMovie.visibility = View.GONE
                                        adapterTop.submitList(Resource.Success(response.data))

                                    }

                                    return true
                                }

                            })
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), response.errorMsg, Toast.LENGTH_SHORT)
                                .show()

                        }
                        is Resource.Loader -> {
                            if (response.isLoading != binding!!.pbHome.isVisible) {
                                binding!!.pbHome.visibility = View.GONE
                                binding!!.tvHomeLoader.visibility = View.GONE

                            }
                        }
                    }
                }
            }
        }

    }

    private fun searchUpcoming() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contentUpcoming.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            var displayList = mutableListOf<UpcomingMoviesModel.Result?>()

                            binding!!.searchAction.setOnQueryTextListener(object :
                                SearchView.OnQueryTextListener {
                                override fun onQueryTextSubmit(query: String?): Boolean {
                                    return true
                                }

                                override fun onQueryTextChange(newText: String?): Boolean {
                                    if (newText!!.isNotEmpty()) {
                                        displayList.clear()
                                        val search = newText.lowercase(Locale.getDefault())
                                        response.data.forEach {
                                            if (it?.title?.lowercase(Locale.getDefault())
                                                    ?.contains(search) == true
                                            ) {
                                                displayList.add(it)
                                            }

                                        }
                                        if (displayList.size == 0) {
                                            binding!!.tvNoMovie.visibility = View.VISIBLE
                                            binding!!.tvNoMovie.setText("Movie $newText is not available")
                                        } else {
                                            binding!!.tvNoMovie.visibility = View.GONE

                                        }

                                        adapterUpcoming = UpcomingMoviesAdapter(requireContext())
                                        adapterUpcoming.submitList(Resource.Success(displayList))
                                        binding!!.rvHomeRecycler.layoutManager =
                                            GridLayoutManager(activity, 2)
                                        binding!!.rvHomeRecycler.adapter = adapterUpcoming
//                                        adapterUpcoming.onClickListener = {
//                                            findNavController().navigate(
//                                                HomeFragmentDirections.actionHomeFragmentToDialogUpcomingFragment(
//                                                    it,
//                                                )
//                                            )
//                                        }

                                    } else {
                                        adapterUpcoming = UpcomingMoviesAdapter(requireContext())
                                        binding!!.rvHomeRecycler.layoutManager =
                                            GridLayoutManager(activity, 2)
                                        binding!!.rvHomeRecycler.adapter = adapterUpcoming
                                        adapter.onClickListener = { item ->
                                            findNavController().navigate(
                                                HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                                                    item,
                                                )
                                            )
                                        }

                                        binding!!.tvNoMovie.visibility = View.GONE
                                        adapterUpcoming.submitList(Resource.Success(response.data))

                                    }

                                    return true
                                }

                            })
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), response.errorMsg, Toast.LENGTH_SHORT)
                                .show()

                        }
                        is Resource.Loader -> {
                            if (response.isLoading != binding!!.pbHome.isVisible) {
                                binding!!.pbHome.visibility = View.GONE
                                binding!!.tvHomeLoader.visibility = View.GONE

                            }
                        }
                    }
                }
            }
        }
    }


    private fun getPopularMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getMovieContent()
                viewModel.contentState.collect {
                    when (it) {
                        is Resource.Success -> {
                            adapter = MovieHomeAdapter(requireContext())
                            adapter.submitList(it)
                            binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                            binding!!.rvHomeRecycler.adapter = adapter
                            adapter.onClickListener = { item ->
                                findNavController().navigate(
                                    HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                                        item,
                                    )
                                )

                            }

                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.errorMsg, Toast.LENGTH_SHORT).show()

                        }
                        is Resource.Loader -> {
                            if (it.isLoading != binding!!.pbHome.isVisible) {
                                binding!!.pbHome.visibility = View.GONE
                                binding!!.tvHomeLoader.visibility = View.GONE

                            }
                        }
                    }

                }
            }
        }
    }

    private fun getUpcomingMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getUpComingMovieContent()
                viewModel.contentUpcoming.collect {
                    when (it) {
                        is Resource.Success -> {
                            adapterUpcoming = UpcomingMoviesAdapter(requireContext())
                            adapterUpcoming.submitList(it)
                            binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                            binding!!.rvHomeRecycler.adapter = adapterUpcoming
//                            adapterTop.onClickListener = {
//                                findNavController().navigate(
//                                    HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                        it
//                                    )
//                                )
//
//                            }

                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.errorMsg, Toast.LENGTH_SHORT).show()

                        }
                        is Resource.Loader -> {
                            if (it.isLoading != binding!!.pbHome.isVisible) {
                                binding!!.pbHome.visibility = View.GONE
                                binding!!.tvHomeLoader.visibility = View.GONE

                            }
                        }
                    }

                }
            }
        }
    }

    private fun getTopRatedMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTopMovieContent()
                viewModel.contentTopRated.collect {
                    when (it) {
                        is Resource.Success -> {
                            adapterTop = MovieTopRatedAdapter(requireContext())
                            adapterTop.submitList(it)
                            binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                            binding!!.rvHomeRecycler.adapter = adapterTop
//                            adapterTop.onClickListener = {
//                                findNavController().navigate(
//                                    HomeFragmentDirections.actionHomeFragmentToDialogFragment(
//                                        it
//                                    )
//                                )
//
//                            }

                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.errorMsg, Toast.LENGTH_SHORT).show()

                        }
                        is Resource.Loader -> {
                            if (it.isLoading != binding!!.pbHome.isVisible) {
                                binding!!.pbHome.visibility = View.GONE
                                binding!!.tvHomeLoader.visibility = View.GONE

                            }
                        }
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