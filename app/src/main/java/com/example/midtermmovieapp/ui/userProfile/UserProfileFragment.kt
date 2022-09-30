package com.example.midtermmovieapp.ui.userProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth

class UserProfileFragment : Fragment() {

    private var binding: FragmentUserProfileBinding? = null
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        userCheck()
        fromProfileToHome()
        fromProfileToLogIn()
        fromProfileToRegister()


    }

    private fun fromProfileToRegister() {
        binding!!.btnRegister.setOnClickListener {
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToRegisterFragment())
        }
    }


    private fun userCheck() {
        if (auth.currentUser !== null) {
            binding!!.btnSignOut.visibility = View.VISIBLE
            binding!!.btnSignOut.setOnClickListener {
                auth.signOut()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.user_signed_out),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToHomeFragment())
            }
        }

    }


    private fun fromProfileToHome() {
        binding!!.btnBackFromProfile.setOnClickListener {
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToHomeFragment())
        }
    }

    private fun fromProfileToLogIn() {
        binding!!.btnLogin.setOnClickListener {
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToLogInFragment())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}