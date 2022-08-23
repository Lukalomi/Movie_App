package com.example.midtermmovieapp.userProfile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.databinding.FragmentLogInBinding
import com.example.midtermmovieapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogInFragment : Fragment() {
    private var binding: FragmentLogInBinding? = null
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding!!.btnLogin.setOnClickListener {
            logInUser()
        }
    }

    private fun logInUser() {
        if (binding!!.etEmail.text.toString().isNotEmpty() && binding!!.etPass.text.toString()
                .isNotEmpty()
        ) {

            viewLifecycleOwner.lifecycleScope.launch {

                try {
                    binding!!.pgLogIn.visibility = View.VISIBLE

                    auth.signInWithEmailAndPassword(
                        binding!!.etEmail.text.toString(),
                        binding!!.etPass.text.toString()
                    ).addOnCompleteListener {

                        if(it.isSuccessful) {
                            checkLoggedInstance()
                            binding!!.pgLogIn.visibility = View.INVISIBLE

                        }
                        else {
                            Toast.makeText(requireContext(),it.exception.toString(), Toast.LENGTH_LONG).show()
                            binding!!.pgLogIn.visibility = View.INVISIBLE

                        }
                    }


                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }

                }

            }
        }
    }


    private fun checkFields() {
        if (binding!!.etEmail.text.toString().isEmpty() && binding!!.etPass.text.toString()
                .isEmpty())
        {
            Toast.makeText(requireContext(), "Please Fill Out Every Field", Toast.LENGTH_SHORT).show()

        }
    }

    private fun checkLoggedInstance() {
        if (auth.currentUser == null) {
            Toast.makeText(requireContext(), "you Haven't Registered", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "you are Logged In", Toast.LENGTH_SHORT).show()
            findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToHomeFragment())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}