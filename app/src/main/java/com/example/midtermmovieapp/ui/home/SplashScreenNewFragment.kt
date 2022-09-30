package com.example.midtermmovieapp.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.midtermmovieapp.databinding.FragmentSplashScreenNewBinding

class SplashScreenNewFragment : Fragment() {
    private var binding: FragmentSplashScreenNewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenNewBinding.inflate(inflater, container, false)
        return binding!!.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.myLooper()!!).postDelayed({
            findNavController().navigate(SplashScreenNewFragmentDirections.actionSplashScreenNewFragmentToHomeFragment())
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}