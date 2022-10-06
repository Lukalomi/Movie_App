package com.example.midtermmovieapp.ui.home


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.data.local.Movie
import com.example.midtermmovieapp.databinding.FragmentHomeBinding
import com.example.midtermmovieapp.databinding.SingleMovieItemBinding
import com.example.midtermmovieapp.domain.models.HomeModel
import com.example.midtermmovieapp.domain.models.TopRatedMoviesModel
import com.example.midtermmovieapp.domain.models.UpcomingMoviesModel
import com.example.midtermmovieapp.ui.adapters.*
import com.example.midtermmovieapp.ui.favorites.FavoritesViewModel
import com.example.midtermmovieapp.ui.favorites.roomList
import com.example.midtermmovieapp.ui.favorites.roomList2
import com.example.midtermmovieapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: MovieHomeAdapter
    private val viewModel: HomeViewModel by viewModels()
    private val viewModel2: FavoritesViewModel by viewModels()
    private lateinit var searchAdapter: HomeNormalAdapter
    private lateinit var adapterTop: MovieTopRatedAdapter
    private lateinit var adapterUpcoming: UpcomingMoviesAdapter
    lateinit var auth: FirebaseAuth
    private val viewModelFav: FavoritesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        lifecycleScope.launch {
            viewModel2.readAllData().collect {
                it.forEach {
                    roomList.add(it)
                }
                Log.d("roommovie", roomList.size.toString())

            }
        }

//
        binding!!.appCompatImageButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
        }

        goToFavFragment()


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


    private fun hideKeyboard(): Boolean {
        val inputManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            activity!!.currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        return true
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
                                    hideKeyboard()

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
                                        setSearchAdapter()
                                        searchAdapter.submitList(Resource.Success(displayList))

                                    } else {
                                        getPopularMoviesPager()
                                        hideKeyboard()
                                        binding!!.searchAction.clearFocus()

                                    }

                                    return true
                                }
                            }
                            )
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.moviePager.collect {
                    setPopularsAdapter()
                    adapter.submitData(it)
                }

            }
        }

    }


    private fun getTopMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieTopPager.collect {
                    setTopAdapter()
                    adapterTop.submitData(it)


                }
            }

        }

    }


    private fun getUpcomingMoviesPager() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setUpcomingAdapter()

                viewModel.movieUpcomingPager.collect {
                    adapterUpcoming.submitData(it)

                }

            }
        }

    }


    private fun goToFavFragment() {
        binding!!.btnStarFav.setOnClickListener {
            if (auth.currentUser != null) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFavoritesFragment())
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please Sign In To See Your Favorite Movies",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun setSearchDialog(item: HomeModel.Result) {
        val dialogBinding = layoutInflater.inflate(R.layout.fragment_dialog, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        val name = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieName)
        var image: ImageView = dialogBinding.findViewById(R.id.ivDialogMovie)
        val desc = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieDesc)
        val addAndRemove = dialogBinding.findViewById<AppCompatImageButton>(R.id.ivFavLogo)
        var movie = Movie(
            originalTitle = item.originalTitle,
            posterPath = "https://image.tmdb.org/t/p/w500" + item.posterPath,
            overview = item.overview,
            voteAverage = item.voteAverage,
            uid = 0,
            favoritesLogo = resources.getDrawable(R.mipmap.ic_launcher_round).toBitmap()
        )
        name.text = item.title
        desc.text = item.overview
        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w500" + item.posterPath)
            .into(image)
        val builder = AlertDialog.Builder(requireContext())


        if (roomList.size == 0) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if (roomList.size == 1 && roomList[0].originalTitle != movie.originalTitle) {
            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle != movie.originalTitle || roomList[1].originalTitle != movie.originalTitle ) ) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle == movie.originalTitle || roomList[1].originalTitle == movie.originalTitle ) ) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 1 && (roomList[0].originalTitle == movie.originalTitle)) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if(roomList.size > 2) {
            roomList2.clear()
            builder.setPositiveButton("Yes") { _, _ ->
                roomList.add(movie)

                if(roomList2.toString().contains(movie.originalTitle) || roomList.toString().contains(movie.originalTitle) ) {
                    Toast.makeText(requireContext(),"Its already a fav",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }
                if(!roomList2.toString().contains(movie.originalTitle) || !roomList.toString().contains(movie.originalTitle)) {

                    viewModel2.insertMovie(movie)

                    Toast.makeText(
                        requireContext(),
                        "${movie.originalTitle} Has Been Added To Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()

                }
            }
        }

        if(roomList.size > 2 && roomList.toString().contains(movie.originalTitle)) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

        }






        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Add ${movie.originalTitle}?")
        builder.setMessage("Are You Sure You Want To Add ${movie.originalTitle} To Favorite Movies?")
        addAndRemove.setOnClickListener {

            builder.create().show()

        }

        dialog.show()

    }


    private fun setPopularsDialog(item: HomeModel.Result) {
        val dialogBinding = layoutInflater.inflate(R.layout.fragment_dialog, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        val name = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieName)
        var image: ImageView = dialogBinding.findViewById(R.id.ivDialogMovie)
        val desc = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieDesc)
        val addAndRemove = dialogBinding.findViewById<AppCompatImageButton>(R.id.ivFavLogo)
        var movie = Movie(
            originalTitle = item.originalTitle,
            posterPath = "https://image.tmdb.org/t/p/w500" + item.posterPath,
            overview = item.overview,
            voteAverage = item.voteAverage,
            uid = 0,
            favoritesLogo = resources.getDrawable(R.mipmap.ic_launcher_round).toBitmap()
        )
        name.text = item.title
        desc.text = item.overview
        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w500" + item.posterPath)
            .into(image)
        val builder = AlertDialog.Builder(requireContext())


        if (roomList.size == 0) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if (roomList.size == 1 && roomList[0].originalTitle != movie.originalTitle) {
            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle != movie.originalTitle || roomList[1].originalTitle != movie.originalTitle ) ) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle == movie.originalTitle || roomList[1].originalTitle == movie.originalTitle ) ) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

       if (roomList.size == 1 && (roomList[0].originalTitle == movie.originalTitle)) {
           addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if(roomList.size > 2) {
            roomList2.clear()
                builder.setPositiveButton("Yes") { _, _ ->
                    roomList.add(movie)

                    if(roomList2.toString().contains(movie.originalTitle) || roomList.toString().contains(movie.originalTitle) ) {
                        Toast.makeText(requireContext(),"Its already a fav",Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                    }
                    if(!roomList2.toString().contains(movie.originalTitle) || !roomList.toString().contains(movie.originalTitle)) {

                        viewModel2.insertMovie(movie)

                        Toast.makeText(
                            requireContext(),
                            "${movie.originalTitle} Has Been Added To Favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()

                    }
                }
            }

        if(roomList.size > 2 && roomList.toString().contains(movie.originalTitle)) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

        }






        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Add ${movie.originalTitle}?")
        builder.setMessage("Are You Sure You Want To Add ${movie.originalTitle} To Favorite Movies?")
        addAndRemove.setOnClickListener {

            builder.create().show()

        }

        dialog.show()

    }


    private fun setTopRatedDialog(item: TopRatedMoviesModel.Result) {
        val dialogBinding = layoutInflater.inflate(R.layout.fragment_dialog, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        val name = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieName)
        var image: ImageView = dialogBinding.findViewById(R.id.ivDialogMovie)
        val desc = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieDesc)
        val addAndRemove = dialogBinding.findViewById<AppCompatImageButton>(R.id.ivFavLogo)
        var movie = Movie(
            originalTitle = item.originalTitle!!,
            posterPath = "https://image.tmdb.org/t/p/w500" + item.posterPath,
            overview = item.overview!!,
            voteAverage = item.voteAverage!!,
            uid = 0,
            favoritesLogo = resources.getDrawable(R.mipmap.ic_launcher_round).toBitmap()
        )
        name.text = item.title
        desc.text = item.overview
        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w500" + item.posterPath)
            .into(image)
        val builder = AlertDialog.Builder(requireContext())

        if (roomList.size == 0) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if (roomList.size == 1 && roomList[0].originalTitle != movie.originalTitle) {
            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle != movie.originalTitle || roomList[1].originalTitle != movie.originalTitle ) ) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle == movie.originalTitle || roomList[1].originalTitle == movie.originalTitle ) ) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 1 && (roomList[0].originalTitle == movie.originalTitle)) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if(roomList.size > 2) {
            roomList2.clear()
            builder.setPositiveButton("Yes") { _, _ ->
                roomList.add(movie)

                if(roomList2.toString().contains(movie.originalTitle) || roomList.toString().contains(movie.originalTitle) ) {
                    Toast.makeText(requireContext(),"Its already a fav",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }
                if(!roomList2.toString().contains(movie.originalTitle) || !roomList.toString().contains(movie.originalTitle)) {

                    viewModel2.insertMovie(movie)

                    Toast.makeText(
                        requireContext(),
                        "${movie.originalTitle} Has Been Added To Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()

                }
            }
        }

        if(roomList.size > 2 && roomList.toString().contains(movie.originalTitle)) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

        }






        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Add ${movie.originalTitle}?")
        builder.setMessage("Are You Sure You Want To Add ${movie.originalTitle} To Favorite Movies?")
        addAndRemove.setOnClickListener {

            builder.create().show()

        }

        dialog.show()

    }

    private fun setUpcomingDialog(item: UpcomingMoviesModel.Result) {
        val dialogBinding = layoutInflater.inflate(R.layout.fragment_dialog, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        val name = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieName)
        var image: ImageView = dialogBinding.findViewById(R.id.ivDialogMovie)
        val desc = dialogBinding.findViewById<TextView>(R.id.tvDialogMovieDesc)
        val addAndRemove = dialogBinding.findViewById<AppCompatImageButton>(R.id.ivFavLogo)
        var movie = Movie(
            originalTitle = item.originalTitle!!,
            posterPath = "https://image.tmdb.org/t/p/w500" + item.posterPath,
            overview = item.overview!!,
            voteAverage = item.voteAverage!!,
            uid = 0,
            favoritesLogo = resources.getDrawable(R.mipmap.ic_launcher_round).toBitmap()
        )
        name.text = item.title
        desc.text = item.overview
        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w500" + item.posterPath)
            .into(image)
        val builder = AlertDialog.Builder(requireContext())


        if (roomList.size == 0) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if (roomList.size == 1 && roomList[0].originalTitle != movie.originalTitle) {
            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle != movie.originalTitle || roomList[1].originalTitle != movie.originalTitle ) ) {

            builder.setPositiveButton("Yes") { _, _ ->
                viewModel2.insertMovie(movie)
                roomList.add(movie)
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Has Been Added To Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 2 &&(roomList[0].originalTitle == movie.originalTitle || roomList[1].originalTitle == movie.originalTitle ) ) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

        if (roomList.size == 1 && (roomList[0].originalTitle == movie.originalTitle)) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "${movie.originalTitle} Movie Is Already In Favorites",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }
        if(roomList.size > 2) {
            roomList2.clear()
            builder.setPositiveButton("Yes") { _, _ ->
                roomList.add(movie)

                if(roomList2.toString().contains(movie.originalTitle) || roomList.toString().contains(movie.originalTitle) ) {
                    Toast.makeText(requireContext(),"Its already a fav",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                }
                if(!roomList2.toString().contains(movie.originalTitle) || !roomList.toString().contains(movie.originalTitle)) {

                    viewModel2.insertMovie(movie)

                    Toast.makeText(
                        requireContext(),
                        "${movie.originalTitle} Has Been Added To Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()

                }
            }
        }

        if(roomList.size > 2 && roomList.toString().contains(movie.originalTitle)) {
            addAndRemove.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_fav_logo))

        }







        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Add ${movie.originalTitle}?")
        builder.setMessage("Are You Sure You Want To Add ${movie.originalTitle} To Favorite Movies?")
        addAndRemove.setOnClickListener {

            builder.create().show()

        }

        dialog.show()

    }

    private fun setPopularsAdapter() {


        adapter = MovieHomeAdapter(requireContext())
        binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
        binding!!.rvHomeRecycler.adapter = adapter
        adapter.withLoadStateFooter(footer = LoaderAdapter())
        binding!!.pbHome.visibility = View.GONE
        binding!!.tvHomeLoader.visibility = View.GONE
        adapter.onClickListener = { item ->
            setPopularsDialog(item)
            lifecycleScope.launch {
                viewModel2.readAllData().collect {
                    it.forEach {
                        roomList2.add(it)
                    }
                    Log.d("roommovie2", roomList.size.toString())

                }
            }
        }

    }

    private fun setSearchAdapter() {
        searchAdapter = HomeNormalAdapter(requireContext())

        binding!!.rvHomeRecycler.layoutManager =
            GridLayoutManager(activity, 2)
        binding!!.rvHomeRecycler.adapter = searchAdapter


        searchAdapter.onClickListener = { item ->
            setSearchDialog(item)
            lifecycleScope.launch {
                viewModel2.readAllData().collect {
                    it.forEach {
                        roomList2.add(it)
                    }
                    Log.d("roommovie2", roomList.size.toString())

                }
            }
        }
    }

    private fun setTopAdapter() {

        adapterTop = MovieTopRatedAdapter(requireContext())
        binding!!.tvHomeLoader.visibility = View.GONE
        binding!!.pbHome.visibility = View.GONE
        binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
        binding!!.rvHomeRecycler.adapter = adapterTop

        adapterTop.withLoadStateFooter(footer = LoaderAdapter())
        adapterTop.onClickListener = { item ->
            setTopRatedDialog(item!!)
            lifecycleScope.launch {
                viewModel2.readAllData().collect {
                    it.forEach {
                        roomList2.add(it)
                    }
                    Log.d("roommovie2", roomList.size.toString())

                }
            }
        }
    }

    private fun setUpcomingAdapter() {

        adapterUpcoming = UpcomingMoviesAdapter(requireContext())
        binding!!.tvHomeLoader.visibility = View.GONE
        binding!!.pbHome.visibility = View.GONE
        binding!!.rvHomeRecycler.layoutManager = GridLayoutManager(activity, 2)
        binding!!.rvHomeRecycler.adapter = adapterUpcoming
        adapterUpcoming.withLoadStateFooter(footer = LoaderAdapter())
        adapterUpcoming.onClickListener = { item ->
            setUpcomingDialog(item!!)
            lifecycleScope.launch {
                viewModel2.readAllData().collect {
                    it.forEach {
                        roomList2.add(it)
                    }
                    Log.d("roommovie2", roomList.size.toString())

                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        roomList.clear()
        binding = null

    }

}