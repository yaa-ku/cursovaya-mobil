package com.example.fragment_navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.example.fragment_navigation.databinding.FragmentHomeBinding
import com.example.fragment_navigation.databinding.FragmentProfileBinding
import com.example.fragment_navigation.databinding.FragmentWeatherBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        user = FirebaseAuth.getInstance()

        if (user.currentUser != null) {
            user.currentUser?.let {
                binding.tvUserEmail.text = it.email
            }
        }
        binding.btnSignOut.setOnClickListener {
            user.signOut()
            requireActivity().startActivity(Intent(context, Register::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
        return binding.root
    }
    companion object {

        @JvmStatic
        fun newInstance() =
            ProfileFragment()
    }
}