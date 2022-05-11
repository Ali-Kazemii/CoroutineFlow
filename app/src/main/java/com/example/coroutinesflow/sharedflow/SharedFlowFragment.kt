package com.example.coroutinesflow.sharedflow

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.coroutinesflow.databinding.FragmentSharedFlowBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SharedFlowFragment : Fragment() {

    private lateinit var binding: FragmentSharedFlowBinding

    private val viewModel by viewModels<SharedFlowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedFlowBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        collectFlow1()  // <repeatOnLifeCycle>
//        collectFlow2()  // <flowWithLifecycle>
        collectFlow3()    // <Wrapper function>

        collectSharedFlow()

        binding.buttonClear.setOnClickListener {
           binding.textView.text = ""
        }
        binding.buttonStateFlow.setOnClickListener {
            viewModel.incrementCounter()
        }
        binding.buttonSharedFlow.setOnClickListener {
           viewModel.squareNumber(3)
        }
    }


    /**
     * when activity goes to background, "repeatOnLifecycle" cause cancel stateflow's collecting
     * "repeatOnLifecycle" is a suspend function
     *
     * we can use "flowWithLifecycle" too, when we have only one flow to collect
     * This API uses the "repeatOnLifecycle" API under the hood
     *
     * <*>  Using these APIs to safety collect flows from UI layer <*>
     *
     * https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bd
     *
     * https://medium.com/androiddevelopers/repeatonlifecycle-api-design-story-8670d1a7d333
     *
     * The block passed to "repeatOnLifecycle" is executed when the lifecycle
     * is at least STARTED and is cancelled when the lifecycle is STOPPED.
     * It automatically restarts the block when the lifecycle is STARTED again.
     *
     * Safely collect from "Flow" when the lifecycle is STARTED
     * and stops collection when the lifecycle is STOPPED
     *
     * the coroutine that calls "repeatOnLifecycle" won’t resume executing until the lifecycle is destroyed
     **/

    private fun collectFlow1() {
       /** Listen to multiple flows with "repeatOnLifecycle"  **/

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow
                    .collect {
                        binding.textView.text = "Counter: $it"
                    }
            }
        }
    }

    private fun collectFlow2(){
        /**  Listen to one flow in a lifecycle-aware manner using flowWithLifecycle **/
        lifecycleScope.launch {
            viewModel.stateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    binding.textView.text = "Counter: $it"
                }
        }
    }

    private fun collectFlow3(){
        activity?.collectLatestLifecycleFlow(viewModel.stateFlow){
            binding.textView.text = "Counter: $it"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun collectSharedFlow() {
        lifecycleScope.launch {
            viewModel.sharedFlow.collect{
                delay(2000)
                binding.textView.text = "FirstFlow: The Received Number is : $it"
            }
        }
        lifecycleScope.launch {
            viewModel.sharedFlow.collect{
                delay(3000)
                binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n\n" else "" } SecondFlow: The Received Number is : $it"
            }
        }
    }
}

