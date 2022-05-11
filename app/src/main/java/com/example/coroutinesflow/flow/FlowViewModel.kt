package com.example.coroutinesflow.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FlowViewModel : ViewModel() {

    init {

        countDownFlow()
        collectFlow()
//        collectFlowLatest()
    }


    fun countDownFlow(
        startingValue: Int = 10,
        delay: Long = 1000
    ): Flow<Int> = flow {

        var currentValue = startingValue
        emit(startingValue)

        while (currentValue > 0) {
            delay(delay)
            currentValue--
            emit(currentValue)
        }
    }


    /**-------------------------------------------------------------**/

    private fun collectFlow() {
        viewModelScope.launch {
            countDownFlow().collect { time ->
                println("The current time is :=> $time")
            }
        }
    }

    private fun collectFlowLatest() {
        //print just 0 as time, because delay(1500) cause another number comes from countDownFlow
        // and wait 1500ml for print while another number comes
        // so after 0, all block canceled
        // if we use collect rather than collectLatest, numbers print as collectFlow() function

        viewModelScope.launch {
            countDownFlow().collectLatest { time ->
                delay(1500L)
                println("The current time is :=> $time")
            }
        }
    }

    /**----------------------------------------------------------------**/
}