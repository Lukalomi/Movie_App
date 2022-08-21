package com.example.midtermmovieapp.userProfile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

 private var binding: FragmentUserProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.btnLogin.setOnClickListener{
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToLogInFragment())
        }
        binding!!.btnRegister.setOnClickListener{
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToRegisterFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}