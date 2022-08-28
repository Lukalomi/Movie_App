package com.example.midtermmovieapp.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.adapters.*
import com.example.midtermmovieapp.databinding.FragmentHomeBinding
import com.example.midtermmovieapp.utils.Resource
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: MovieHomeAdapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var searchAdapter: HomeNormalAdapter
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
                    getPopularMoviesPager()
                    searchPager()
                    binding!!.tvMovieListType.text = getString(R.string.popular_movies)
                }
                if (position == 1) {
                    getTopMoviesPager()
                    searchPager()
                    binding!!.tvMovieListType.text = getString(R.string.top_rated_movies)
                }

                if (position == 2) {
                    getUpcomingMoviesPager()
                    searchPager()

                    binding!!.tvMovieListType.text = getString(R.string.upcoming_movies)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                getPopularMoviesPager()
                searchPager()
            }

        }

    }


    private fun searchPager() {
        var displayList: MutableList<HomeModel.Result> = mutableListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newState.collect {

                    when (it) {

                        is Resource.Success -> {
                            binding!!.searchAction.setOnQueryTextListener(object :
                                SearchView.OnQueryTextListener {
                                override fun onQueryTextSubmit(query: String?): Boolean {
                                    return true
                                }

                                override fun onQueryTextChange(newText: String?): Boolean {
                                    viewModel.getSearchedMovies(query = newText.toString())
                                    if (newText!!.isNotEmpty()) {
                                        displayList.clear()
                                        var search = newText!!.lowercase(Locale.getDefault())
                                        it.data.forEach {
                                            if (it.title.lowercase(Locale.getDefault())
                                                    .contains(search)
                                            ) {
                                                displayList.add(it)
                                            }
                                        }
                                        val anim = LayoutAnimationController(
                                            AnimationUtils.loadAnimation(
                                                requireContext(),
                                                R.anim.recycler_anim
                                            )
                                        )
                                        anim.delay = 0.20f
                                        anim.order = LayoutAnimationController.ORDER_NORMAL
                                        searchAdapter = HomeNormalAdapter(requireContext())
                                        searchAdapter.submitList(Resource.Success(displayList))
                                        binding!!.rvHomeRecycler.layoutManager =
                                            GridLayoutManager(activity, 2)
                                        binding!!.rvHomeRecycler.adapter = searchAdapter
                                        binding!!.rvHomeRecycler.layoutAnimation = anim


                                        searchAdapter.onClickListener = {
                                            findNavController().navigate(
                                                HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                                                    it
                                                )
                                            )
                                        }

                                        if (displayList.size > 0) {
                                            binding!!.pbHome.visibility = View.GONE

                                        }
                                        if (displayList.size == 0) {
                                            binding!!.pbHome.visibility = View.VISIBLE

                                        }


                                    } else {
                                        getPopularMoviesPager()
                                    }


                                    return true

                                }

                            })
                        }

                        is Resource.Error -> {
                            Log.d(getString(R.string.error), it.errorMsg)
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

    private fun getPopularMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviePager.collect {
                    binding!!.pbHome.visibility = View.GONE
                    var anim = LayoutAnimationController(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.recycler_anim
                        )
                    )
                    anim.delay = 0.10f
                    anim.order = LayoutAnimationController.ORDER_RANDOM
                    binding!!.tvHomeLoader.visibility = View.GONE
                    adapter = MovieHomeAdapter(requireContext())
                    binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                    binding!!.rvHomeRecycler.layoutAnimation = anim
                    binding!!.rvHomeRecycler.adapter = adapter
                    adapter.withLoadStateFooter(footer = LoaderAdapter())
                    adapter.submitData(it)


                }
            }
        }
        adapter.onClickListener = { item ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                    item
                )
            )

        }
    }


    private fun getTopMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieTopPager.collect {
                    adapterTop = MovieTopRatedAdapter(requireContext())
                    var anim = LayoutAnimationController(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.recycler_anim
                        )
                    )
                    binding!!.tvHomeLoader.visibility = View.GONE
                    binding!!.pbHome.visibility = View.GONE
                    anim.delay = 0.20f
                    anim.order = LayoutAnimationController.ORDER_NORMAL
                    binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                    binding!!.rvHomeRecycler.adapter = adapterTop
                    binding!!.rvHomeRecycler.layoutAnimation = anim
                    adapterTop.withLoadStateFooter(footer = LoaderAdapter())
                    adapterTop.submitData(it)


                }
            }

        }
        adapterTop.onClickListener = {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToTopRatedDialogFragment(
                    it
                )
            )
        }
    }


    private fun getUpcomingMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieUpcomingPager.collect {
                    var anim = LayoutAnimationController(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.recycler_anim
                        )
                    )
                    anim.delay = 0.20f
                    anim.order = LayoutAnimationController.ORDER_NORMAL
                    binding!!.tvHomeLoader.visibility = View.GONE
                    binding!!.pbHome.visibility = View.GONE
                    adapterUpcoming = UpcomingMoviesAdapter(requireContext())
                    binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                    binding!!.rvHomeRecycler.adapter = adapterUpcoming
                    binding!!.rvHomeRecycler.layoutAnimation = anim
                    adapterUpcoming.withLoadStateFooter(footer = LoaderAdapter())
                    adapterUpcoming.submitData(it)

                }

            }
        }
        adapterUpcoming.onClickListener = { item ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToUpcomingDialogFragment(
                    item
                )

            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}