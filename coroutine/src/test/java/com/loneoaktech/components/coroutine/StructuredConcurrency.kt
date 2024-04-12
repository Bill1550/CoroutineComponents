package com.loneoaktech.components.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class StructuredConcurrency {

    private suspend fun doSomeWork(arg: Int): Int {
        delay(100)
        return 5*arg
    }

    private val doneFlag = AtomicBoolean(false)

    /**
     * The coroutineScope (small c) exposes the scope that is implicit
     * in the enclosing suspend function, and allows a child job to be created.
     */
    @OptIn(ExperimentalTime::class)
    private suspend fun superviseChildren() {

        coroutineScope {
            launch(Dispatchers.Default) {
                measureTimedValue {
                    (1..3).map { async { doSomeWork(it) } }
                        .awaitAll().sum()
                }.also { (v, d) ->
                    doneFlag.set(true)
                    println("- Computed $v in $d")
                }
            }
        }
    }

    /**
     * CoroutineScope (with capital C) creates a whole new scope (like GlobalScope)
     * with a nwe job, that is not a descendant of the context in the enclosing
     * suspend functions. Because no Job is specified in the example, a new job
     * stand alone job is created
     */
    @OptIn(ExperimentalTime::class)
    private suspend fun freeRangeChildren() {
        CoroutineScope(Dispatchers.Default).launch {
            measureTimedValue {
                (1..3).map { async { doSomeWork(it) } }
                    .awaitAll().sum()
            }.also { (v, d) ->
                doneFlag.set(true)
                println("- Computed $v in $d")
            }
        }
    }

    @Test
    fun `test supervised children`() = runBlocking {
        doneFlag.set(false)
        println("--- Starting")
        superviseChildren()
        println("-- run children done")
        assertTrue(doneFlag.get(), "Done should have been set!")
        delay(500)
    }

    @Test
    fun `test free range children`() = runBlocking {
        doneFlag.set(false)
        println("--- Starting")
        freeRangeChildren()
        println(" run children done")
        assertFalse(doneFlag.get(), "Done should net yet be set")
        delay(500) // let children complete for log.
        assertTrue(doneFlag.get(), "Done should have been set!")
    }
}