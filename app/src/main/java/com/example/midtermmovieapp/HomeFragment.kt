package com.example.midtermmovieapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: MovieHomeAdapter
    private val viewModel: HomeViewModel by viewModels()

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

        search()


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getMovieContent()
                viewModel.contentState.collect {
                    when (it) {
                        is Resource.Success -> {
                            adapter = MovieHomeAdapter(requireContext())
                            adapter.submitList(it)
//                            adapter = MovieHomeAdapter(it, requireContext())
                            binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
                            binding!!.rvHomeRecycler.adapter = adapter
                            adapter.onClickListener = { item ->
                                findNavController().navigate(
                                    HomeFragmentDirections.actionHomeFragmentToDialogFragment(
                                        item
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



    private  fun search() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMovieContent()

            viewModel.contentState.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        var displayList = response.data

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
                                            Log.d("datasize", displayList.size.toString())
                                        }

                                    }
                                    adapter = MovieHomeAdapter(requireContext())
                                    adapter.submitList(Resource.Success(displayList))
                                    binding!!.rvHomeRecycler.layoutManager =
                                        GridLayoutManager(activity, 2)
                                    binding!!.rvHomeRecycler.adapter = adapter
                                } else {
                                    adapter.submitList(Resource.Success(displayList))
                                }

                                return true
                            }

                        })
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