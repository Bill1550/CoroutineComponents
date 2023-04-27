package com.loneoaktech.components.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BasicFlowExperiments {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun aSimpleFlow() = runBlocking(Dispatchers.IO.limitedParallelism(1)) {

        val shareScope = CoroutineScope(this.coroutineContext[CoroutineDispatcher.Key]!!)//Dispatchers.IO)
        val f1 = flowOf(1,2,3)
            .onStart { println("flow started on thread ${Thread.currentThread().name}") }
            .map { delay(100); it}
            .onEach { println("emitting $it") }
            .shareIn(
                shareScope,
                SharingStarted.WhileSubscribed(2000),
            replay = 1
            ).onSubscription { println("subscription") }

        launch {
            println("launch 1 started on thread ${Thread.currentThread().name}")
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
            println("launch 1 started on thread ${Thread.currentThread().name}")
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