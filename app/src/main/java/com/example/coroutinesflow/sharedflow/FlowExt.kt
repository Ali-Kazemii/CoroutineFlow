package com.example.coroutinesflow.sharedflow

import androidx.core.app.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * this is a bad wrapper function because its not follow structured concurrency like "addRepeatingJob"
 * function in flow and both of them aren't suspend function
 *
 *  It doesn’t respect the calling context and can be dangerous to use inside other coroutines
 *  It also will NOT get canceled when you call job.cancel() same as "addRepeatingJob"
 *  This can lead to very subtle bugs in your app which are really difficult to debug.
 * **/
inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}

/**
 * instead using "addRepeatingJob" or "launchAndCollectIn" that mentioned above,
 *  we must use "repeatOnLifecycle"
 *
 * The block passed to repeatOnLifecycle is executed when the lifecycle
 * is at least STARTED and is cancelled when the lifecycle is STOPPED.
 * It automatically restarts the block when the lifecycle is STARTED again.
 **/




fun <T> ComponentActivity.collectLatestLifecycleFlow(
    flow: Flow<T>,
    collect: suspend (T) -> Unit
){
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}