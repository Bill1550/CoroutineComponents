package com.loneoaktech.components.coroutine

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutineBasics {

    suspend fun delayPrint( msg: String ) {
        delay(500)
        println(msg)
    }


    @Test
    fun `print hello world`() = runBlocking {

        measureTimeMillis {
            delayPrint("hello")
            println("world")
        }.also { println("elapsed time=$it") }

        Unit
    }



    @Test
    fun `print hello world interleaved`() = runBlocking {

        measureTimeMillis {
            launch { delayPrint("world") }
            println("hello")
        }.also { println("elapsed time=$it") }

        Unit
    }


    @Test
    fun `print hello world interleaved and wait`() = runBlocking {

        measureTimeMillis {
            val job = launch { delayPrint("world") }
            println("hello")
            job.join()
        }.also { println("elapsed time=$it") }

        Unit
    }

    @Test
    fun `print hello world interleaved and wait differently`() = runBlocking {

        measureTimeMillis {
            coroutineScope {
                launch { delayPrint("world") }
                println("hello")
            }
        }.also { println("elapsed time=$it") }

        Unit
    }
}