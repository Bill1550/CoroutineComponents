package com.loneoaktech.components.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
class SharedFlowTests {


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `shareIn collected to list`() = runTest {
        println("dispatcher=${coroutineContext[CoroutineDispatcher.Key]}") // worked with both standard and unconfined

        val sourceList = listOf(1,2,3,4)

        val coldSource = sourceList.asFlow().map { delay(10); it }

        val sf = coldSource.shareIn(
            scope = backgroundScope,
            started = SharingStarted.Eagerly, //WhileSubscribed(1000),
            replay = 1
        )

        val deferred = async {
            sf.take(4)
                .onEach { println("collected $it")}
                .toList()
        }
        println("collect started, time=$currentTime")
        val results = deferred.await()
        println("Results: $results")
        println("done: time=$currentTime")
        assertEquals(40, currentTime)
        assertEquals(sourceList, results)
    }

    @Test
    fun `shareIn collect in loop`() = runTest {
        val sourceList = listOf(1,2,3,4)

        val coldSource = sourceList.asFlow().map { delay(10); it }

        val sf = coldSource.shareIn(
            scope = backgroundScope,
            started = SharingStarted.WhileSubscribed(1000),
            replay = 1
        )

        val results = mutableListOf<Int>()

        val job = launch {
            sf.collect {
                results.add(it)
            }
        }
        println("collect started, time=$currentTime")
        advanceTimeBy(41) //
        advanceUntilIdle()
        println("Results: $results")
        println("done: time=$currentTime")

        println("background job is active=${job.isActive}") // ShareIn isn't passing the completion event
        job.cancelAndJoin() // ShareIn isn't passing the completion event
//        assertEquals(40, currentTime)
        assertEquals(sourceList, results)
    }
}