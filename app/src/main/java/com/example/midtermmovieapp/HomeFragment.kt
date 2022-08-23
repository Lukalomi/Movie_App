package com.example.midtermmovieapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.midtermmovieapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

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


        viewModel.getMovieContent()


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contentState.collect {
                    when (it) {
                        is Resource.Success -> {
                            adapter = MovieHomeAdapter(it, requireContext())
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


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}