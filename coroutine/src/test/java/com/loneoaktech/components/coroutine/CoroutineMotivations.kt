package com.loneoaktech.components.coroutine

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 * Examples of why coroutines
 */
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class CoroutineMotivations {

    val reps = 10_000

    @Test
    fun `run a lot of threads`() {

        measureTimeMillis {

            val threads = List(reps) {
                thread {
                    Thread.sleep(1000)
                    if (it % (reps / 10) == 0)
                        println("Thread Thread$it is done, name=${Thread.currentThread().name}")
                }
            }
            println("Threads started")
            threads.forEach { it.join() }
        }.also { println("Time for $reps threads=$it") }
    }


    @Test
    fun `run a lot of coroutines`() = runBlocking(Dispatchers.Default) {

        measureTimeMillis {
            val jobs = List(reps) {
                launch {
                    delay(1000)
                    if (it % (reps / 10) == 0)
                        println("Coroutine $it done, thread=${Thread.currentThread().name}")
                }
            }
            println("Coroutines started")
            jobs.joinAll()
        }.also { println("Time $reps coroutines, default dispatcher=$it") }

        Unit
    }

    @Test
    fun `run a lot of coroutines on one thread`() = runBlocking(Dispatchers.IO.limitedParallelism(1)) {
            measureTimeMillis {
            val jobs = List(reps) {
                launch {
                    delay(1000)
                    if (it % (reps / 10) == 0)
                        println("Coroutine $it done, thread=${Thread.currentThread().name}")
                }
            }
            println("Coroutines started")
            jobs.joinAll()
        }.also { println("Time for $reps coroutines, single thread=$it") }

        Unit
    }

    @Test
    fun `run a lot of coroutines on 2 threads`() = runBlocking(newFixedThreadPoolContext(2, "two thread context") ) {

        measureTimeMillis {
            val jobs = List(reps) {
                launch {
                    delay(1000)
                    if (it % (reps / 10) == 0)
                        println("Coroutine $it done, thread=${Thread.currentThread().name}")
                }
            }
            println("Coroutines started")
            jobs.joinAll()
        }.also { println("Time time for $reps coroutines, 2 threads=$it") }

        Unit
    }
}
