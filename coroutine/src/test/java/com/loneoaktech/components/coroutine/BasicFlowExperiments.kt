package com.loneoaktech.components.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BasicFlowExperiments {

    @Test
    fun aSimpleFlow() = runBlocking {

        val shareScope = CoroutineScope(Dispatchers.IO)
        val f1 = flowOf(1,2,3)
            .onStart { println("flow started") }
            .onEach { println("emitting $it") }
            .shareIn(
                shareScope,
                SharingStarted.WhileSubscribed(2000),
            replay = 1
            ).onSubscription { println("subscription") }

        launch {
            withTimeoutOrNull(1000) {
                f1.take(3).collect {
                    println("F1: $it")
                    yield()
                }
            }

            println("collect done 1")
        }

//       delay(100)
        launch {
            withTimeoutOrNull( 1000) {
                f1.take(3).collect {
                    println("F2: $it")
                    yield()
                }
            }

            println("collect done 2")
        }

        Unit
    }
}