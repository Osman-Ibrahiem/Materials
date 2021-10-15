package com.osman.nagwa_task.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.osman.nagwa_task.R
import com.osman.nagwa_task.databinding.FragmentHomeBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.recycler) { view, insets ->
            binding.bottomBar.layoutParams = binding.bottomBar.layoutParams.apply {
                height = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            }
            view.updatePadding(bottom = view.paddingBottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.run {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
            setHasFixedSize(true)
            adapter = MaterialsAdapter()
        }
    }
}