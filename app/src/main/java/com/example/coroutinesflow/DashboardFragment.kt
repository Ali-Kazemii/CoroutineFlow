package com.example.coroutinesflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.coroutinesflow.databinding.FragmentDashboardBinding

class DashboardFragment: Fragment() {

    private lateinit var binding: FragmentDashboardBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFlow.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToFlowFragment()
            )
        }
        binding.buttonStateSharedFlow.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToStateShareFlow()
            )
        }
    }
}