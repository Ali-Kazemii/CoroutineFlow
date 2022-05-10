package com.example.coroutinesflow

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.coroutinesflow.databinding.FragmentDashboardBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnClickListeners()
    }

    private fun handleOnClickListeners() {
        binding.btnClear.setOnClickListener {
            binding.textView.text = ""
        }

        binding.btnCountDown.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                callCounter()
            }
        }

        binding.btnFilterMap.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                filterMapCounter()
            }
        }
        binding.btnOnEach.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                onEachCounter()
            }
        }
        binding.btnTerminalCount.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                countNumber()
            }
        }
        binding.btnTerminalReduce.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                reduce()
            }
        }
        binding.btnTerminalFold.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                fold()
            }
        }
        binding.btnFlatMap.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                flatMap()
            }
        }
        binding.btnRestaurant.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                flowRestaurantReceipt()
            }
        }
        binding.btnRestaurantCollectLatest.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                flowRestaurantReceiptCollectLatest()
            }
        }
        binding.btnRestaurantBufferConflate.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                flowRestaurantBufferConflate()
            }
        }
    }

    private suspend fun callCounter() {
        viewModel.countDownFlow()
            .collect {
                binding.textView.text = it.toString()
            }
    }

    private suspend fun filterMapCounter() {
        viewModel.countDownFlow(startingValue = 18)
            .filter { time ->
                time % 2 == 0
            }
            .map { time ->
                time * time
            }
            .collect {
                binding.textView.text = it.toString()
            }
    }

    private fun onEachCounter() {
        viewModel.countDownFlow()
            .onEach {
                binding.textView.text = it.toString()

            }.launchIn(lifecycleScope)
    }

    private suspend fun countNumber() {

        /*val count = viewModel.countDownFlow(startingValue = 18, delay = 0)
            .filter { time ->
                time % 3 == 0
            }
            .count()*/

        val count = viewModel.countDownFlow(startingValue = 18, delay = 0)
            .count { time ->
                time % 3 == 0
            }

        binding.textView.text = "The count % 3 is = $count"
    }

    private suspend fun reduce() {
        val reduceResult = viewModel.countDownFlow(startingValue = 5, delay = 0)
            .reduce { accumulator, value ->
                //sum 1 to 5
                accumulator + value
            }
        binding.textView.text = "The reduce operation = $reduceResult"

    }

    private suspend fun fold() {
        val reduceResult = viewModel.countDownFlow(startingValue = 5, delay = 0)
            .fold(initial = 100) { accumulator, value ->
                //sum 1 to 5 then plus with initial = 100
                accumulator + value
            }
        binding.textView.text = "The fold operation = $reduceResult"

    }

    @SuppressLint("SetTextI18n")
    private suspend fun flatMap() {

        //flatMapConcat get first emit value from flow1, then run his operation completely,
        // after then emit another value from flow1 and so on...

        val flow1 = flow {
            emit(1)
            delay(500)
            emit(2)
        }

        flow1.flatMapConcat { value ->
            flow {
                emit(value + 1)
                delay(500)
                emit(value + 2)
            }
        }.collect {
            val str = StringBuffer()
            str.append("The fold operation = $it")
            binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n" else "" } $str"
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun flowRestaurantReceipt() {
        val flow = flow{
            delay(250)
            emit("Appetizer")
            delay(1000)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }


        flow.onEach {
            binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n\n" else "" } $it is delivered"
        }
            .collect{
                binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n" else "" } Now eating $it"

                delay(1500)

                binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n" else "" } Finish eating $it"

            }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun flowRestaurantReceiptCollectLatest() {
        val flow = flow{
            delay(250)
            emit("Appetizer")
            delay(1000)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }


        flow.onEach {
            binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n\n" else "" } $it is delivered"
        }
            .collectLatest{
                binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n" else "" } Now eating $it"

                delay(1500)

                binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n" else "" } Finish eating $it"

            }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun flowRestaurantBufferConflate() {
        val flow = flow{
            delay(250)
            emit("Appetizer")
            delay(1000)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }


        flow.onEach {
            binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n\n" else "" } $it is delivered"
        }
//            .buffer()
            .conflate()
            .collect{
                binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n" else "" } Now eating $it"

                delay(1500)

                binding.textView.text = "${binding.textView.text} ${if(binding.textView.text.toString().isNotEmpty()) "\n" else "" } Finish eating $it"

            }
    }

}