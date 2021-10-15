package com.osman.materials.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.osman.materials.databinding.FragmentSplashBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SplashFragment @Inject constructor() : DaggerFragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logo.animateLogo(viewLifecycleOwner.lifecycle, 3, ::navigateToHome)
    }

    private fun navigateToHome() {
        val action = SplashFragmentDirections.actionToHome()
        findNavController().navigate(action)
    }

}