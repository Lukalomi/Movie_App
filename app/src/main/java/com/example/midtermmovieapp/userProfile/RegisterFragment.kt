package com.example.midtermmovieapp.userProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.midtermmovieapp.R
import com.example.midtermmovieapp.utils.ResUtils
import com.example.midtermmovieapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding!!.btnRegister.setOnClickListener {
            registerUser()
            checkFields()

        }

    }

    private fun registerUser() {
//        checkFields()
        if (binding!!.etEmail.text.toString().isNotEmpty() && binding!!.etPass.text.toString()
                .isNotEmpty()
        ) {

            viewLifecycleOwner.lifecycleScope.launch {

                try {
                    binding!!.pgRegister.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(
                        binding!!.etEmail.text.toString(),
                        binding!!.etPass.text.toString()
                    ).addOnCompleteListener {

                        if(it.isSuccessful) {
                            checkLoggedInstance()
                            binding!!.pgRegister.visibility = View.GONE
                            sendUserInfo()
                        }
                        else {
                            Toast.makeText(requireContext(),it.exception.toString(),Toast.LENGTH_LONG).show()
                            binding!!.pgRegister.visibility = View.GONE

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
            Toast.makeText(requireContext(), getString(R.string.fill_out_every_field), Toast.LENGTH_SHORT).show()

        }
    }

    private fun checkLoggedInstance() {
        if (auth.currentUser == null) {
            Toast.makeText(requireContext(), getString(R.string.havent_registered), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.you_are_registered), Toast.LENGTH_SHORT).show()
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLogInFragment())
        }
    }

    private fun sendUserInfo()   {
        setFragmentResult(
             ResUtils.AUTH_KEY,
            result = bundleOf(
                ResUtils.EMAIL to binding!!.etEmail.text.toString(),
            ResUtils.PASSWORD to binding!!.etPass.text.toString())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}