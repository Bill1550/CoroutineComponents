package com.loneoaktech.components.coroutine

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutineBasics {

    suspend fun delayPrint( msg: String ) {
        println("delay printing..")
        delay(3000)
//        Thread.sleep(3000)
        println(msg)
    }

    suspend fun getMessage(): String {
        println("getting...")
        delay(500)
        return "Hello"
    }

    @Test
    fun `print hello world`() = runBlocking {

        measureTimeMillis {
            val msg = getMessage()
            delayPrint(msg)
            println("world")
        }.also { println("elapsed time=$it") }

        Unit
    }



    @Test
    fun `print hello world interleaved`() = runBlocking {
        println("outer coroutine=${Thread.currentThread().name}")
        measureTimeMillis {
            launch {
                println("Inner ${Thread.currentThread().name}")
                delayPrint(getMessage()) }
            println("hello 1")
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