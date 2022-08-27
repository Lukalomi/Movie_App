package com.example.midtermmovieapp

import android.content.ContentValues.TAG
import android.content.Loader
import android.os.Bundle
import android.util.Log
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
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import com.example.midtermmovieapp.Models.HomeModel
import com.example.midtermmovieapp.adapters.*
import com.example.midtermmovieapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: MovieHomeAdapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var searchAdapter: HomeNormalAdapter
    private lateinit var adapterTop: MovieTopRatedAdapter
    private lateinit var adapterUpcoming: UpcomingMoviesAdapter
    private var firebase = Firebase.firestore.collection("Movie")
    private lateinit var firebaseAuth: FirebaseAuth
    private var isInMyFavorites = false
    private var firebaseDB = Firebase.database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding!!.btnFavorite.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
        }
        binding!!.btnFavorite.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
        }

        binding!!.appCompatImageButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
        }
//        firebaseAuth = FirebaseAuth.getInstance()
//        if(firebaseAuth.currentUser != null) {
//            checkIsFavorite()
//        }
//
//        binding!!.appCompatImageButton.setOnClickListener {
//            if (firebaseAuth.currentUser == null) {
//                Toast.makeText(requireContext(), "You're Not LoggedIn", Toast.LENGTH_SHORT).show()
//            }else{
//                if (isInMyFavorites){
//                    removeFromFavorites()
//                }else{
//                    addToFavorites()
//                }
//            }
//        }


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
            }

        }

    }


    private fun searchPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newState.collect {
                    var displayList: MutableList<HomeModel.Result> = mutableListOf()

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
                                        var search = newText!!.lowercase(Locale.getDefault())
                                        it.data.forEach {
                                            if (it.title.lowercase(Locale.getDefault())
                                                    .contains(search)
                                            ) {
                                                displayList.add(it)
                                            }
                                        }

                                        searchAdapter = HomeNormalAdapter(requireContext())
                                        searchAdapter.submitList(Resource.Success(displayList))
                                        binding!!.rvHomeRecycler.layoutManager =
                                            GridLayoutManager(activity, 2)
                                        binding!!.rvHomeRecycler.adapter = searchAdapter
                                        if (displayList.size > 0) {
                                            binding!!.pbHome.visibility = View.GONE

                                        }
                                        if(displayList.size < 0) {
                                            binding!!.pbHome.visibility = View.GONE

                                        }


                                    } else {
                                        getPopularMoviesPager()
                                    }


                                    return true

                                }

                            })
                        }

                        is Resource.Error -> {
                            Log.d("Error", it.errorMsg)
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
                    binding!!.tvHomeLoader.visibility = View.GONE
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
//    private fun checkIsFavorite(){
//        val ref = FirebaseDatabase.getInstance().getReference("Users")
//        ref.child(firebaseAuth.uid!!).child("Favorites").child(getPopularMoviesPager().toString())
//            .addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapthot: DataSnapshot) {
//                    isInMyFavorites = snapthot.exists()
//                    if (isInMyFavorites){
//                        Log.d(TAG,"onDataChange: Available in favorite")
//                        binding!!.btnFavorite.setOnClickListener {  }
//                        binding!!.btnFavorite.text = "Remove Favorite"
//                    }else{
//                        Log.d(TAG,"onDataChange: Not Available in favorite")
//                        binding!!.btnFavorite.setOnClickListener {  }
//                        binding!!.btnFavorite.text = "Add Favorite"
//                    }
//                }
//                override fun onCancelled(error: DatabaseError){
//
//                }
//            })
//    }
//
//    private fun addToFavorites(){
//        Log.d(TAG, "addToFavorites: Add to fav")
//        val timestamp = System.currentTimeMillis()
//        val hashMap = HashMap <String, Any>()
//        hashMap["movieId"] = getPopularMoviesPager()
//        hashMap["timestamp"] = timestamp
//
//        val ref = FirebaseDatabase.getInstance().getReference("Users")
//        ref.child(firebaseAuth.uid!!).child("Favorites").child(getPopularMoviesPager().toString())
//            .setValue(hashMap)
//            .addOnSuccessListener {
//                Log.d(TAG, "addToFavorites: add to fav")
//                Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
//
//            }
//            .addOnFailureListener { e->
//                Log.d(TAG, "add to favorites: failed to add to ${e.message}")
//                Toast.makeText(requireContext(), "failed to add to ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//    private fun removeFromFavorites(){
//        Log.d(TAG, "removeFromFavorites: Removing from fav")
//        val ref = FirebaseDatabase.getInstance().getReference("Users")
//        ref.child(firebaseAuth.uid!!).child("Favorites").child(getPopularMoviesPager().toString())
//            .removeValue()
//            .addOnSuccessListener {
//                Log.d(TAG, "removeFromFavorites: Removed from fav")
//                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
//
//            }
//            .addOnFailureListener { e->
//                Log.d(TAG, "removeFromFavorites: Failed to remove from fav due to ${e.message}")
//                Toast.makeText(requireContext(), "failed to remove from fav due to ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}