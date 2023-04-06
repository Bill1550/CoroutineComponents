package com.loneoaktech.components.coroutine.basic

import android.os.Handler
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class BasicThreadSourceTests {

    /**
     * A minimal class that spins up a separate thread and then returns
     * the posted item a few moments later via a callback.
     */
    class DelayedCallback<T>(
        val delay: Long,
    ) {
        private var _listener: ((T) -> Unit)? = null  // should be weak reference

        private var handler: Handler? = null
        private val thread = object : Thread("delay-thread") {
            override fun run() {
                Looper.prepare()
                handler = Looper.myLooper()?.let {
                    object : Handler(it) {}
                }

                Looper.loop()
                println("exiting delay thread")
            }
        }

        fun setListener(listener: ((T) -> Unit)) {
            _listener = listener
        }

        fun sendItem(item: T) {
            println("sending $item")
            handler?.postDelayed(
                object : Runnable {
                    override fun run() {
                        println("calling listener")
                        _listener?.invoke(item)
                    }
                },
                delay
            ) ?: throw IllegalStateException("Handler is not created")
        }

        fun close() {
            handler?.post { Looper.myLooper()?.quitSafely() }
            Thread.sleep(50)
        }

        init {
            thread.start()
            Thread.sleep(50) // give thread time to actually start
        }
    }


    @Test
    fun testCallback() {

        println("test started")
        val strCallback = DelayedCallback<String>(100)
        strCallback.setListener { println("Received: $it") }
        strCallback.sendItem("Hi")
        Thread.sleep(200)
        strCallback.close()
    }

    @Test
    fun callBackFlowTest() = runBlocking {

        val strCallback = DelayedCallback<String>(100)

        val flow = callbackFlow {
            println("flow builder started")
            strCallback.setListener { trySend(it) }
            awaitClose { strCallback.close() }
        }

        val job = launch {
            flow.collect {
                println("collected: $it")
            }
        }

        delay(10)
        strCallback.sendItem("one")
        delay(500)
        job.cancel()
    }

    @Test
    fun sharedFlowTest() = runBlocking {
        val strCallback = DelayedCallback<String>(100)

        val flow = callbackFlow {
            println("inside flow builder")
            strCallback.setListener { trySend(it) }
            awaitClose { strCallback.close() }
        }.shareIn(
            CoroutineScope(Job() + Dispatchers.IO),
            started = SharingStarted.WhileSubscribed()
        ).apply {
            (this as? MutableSharedFlow)?.subscriptionCount?.let { sc ->
                launch {
                    sc.collect {
                        println("Subscription count=$it")
                    }
                }
            }

        }

        println("subscribing to flow")

        val job = launch(Dispatchers.Main) {
            flow.collect {
                println("collected: $it")
            }
        }

        delay(10)
        strCallback.sendItem("one")
        delay(10)
        strCallback.sendItem("two")
        delay(100)
        job.cancel()
        delay(500)
        println("done")
    }

//    private val flowOfEvents = flowOf(
//        "Event 1",
//        "Event 2"
//    )

    private val flowOfEvents = flow {
        println("flow builder started")
        emit("Event 1")
        emit("Event 2")
    }

    // NOTE: stateIn(scope) and stateIn(scope,started,initial) behave differently

    @Test
    fun stateFlowExample1() = runTest {
        val sharingScope = backgroundScope
        // Changing started to SharingStarted.Eagerly doesn't change the behavior of this test.
        val stateFlow = flowOfEvents.stateIn(
            sharingScope,
            started = SharingStarted.WhileSubscribed(),
            "initial"
        )
        println("initial=${stateFlow.value}")
        stateFlow.test {
            val item0 = awaitItem()
            println("first item=$item0")
            assertEquals("initial", item0)

            val item1 = awaitItem()
            println("second item=$item1")
            assertEquals("Event 1", item1)

            val item2 = awaitItem()
            println("third item=$item2")
            assertEquals("Event 2", item2)
        }
    }

    @Test
    fun stateFlowExample2() = runTest {
        val sharingScope = backgroundScope
        val stateFlow = flowOfEvents.stateIn(sharingScope)
        println("initial=${stateFlow.value}")
        stateFlow.test {
            val item = awaitItem()
            println("first item=$item")
            assertEquals("Event 2", item)

            expectNoEvents()
        }
    }

    // needs to be on runBlocking because delays inside callback are hard thread delays
    @Test
    fun secondSuspendCallTest() = runBlocking {

            val strCallback = DelayedCallback<String>(100)

            val flow = callbackFlow {
                println("flow builder started")
                strCallback.setListener { trySend(it) }
                awaitClose { strCallback.close() }
            }

            suspend fun loadDependentData(key: String): String {
                delay(50)
                return "$key-dependent"
            }

            val dependantFlow = flow.map { loadDependentData(it) }

            val job = launch(Dispatchers.Unconfined) {
                println("collector started")
                dependantFlow.collect {
                    println("collected: $it")
                }
            }

            delay(10)
            strCallback.sendItem("one")
            delay(500)
            job.cancel()
        }

    @OptIn(FlowPreview::class)
    @Test
    fun concatenatePrimer() = runBlocking {

        val strCallback = DelayedCallback<String>(100)

        val dataFlow = callbackFlow {
            println("flow builder started")
            strCallback.setListener { trySend(it) }
            awaitClose { strCallback.close() }
        }

        val initialFlow = flow {
            delay(10)
            emit("Initial")
        }

        val flowWithInitial = flowOf( initialFlow, dataFlow).flattenConcat()

        val collected = mutableListOf<String>()
        val job = launch(Dispatchers.Unconfined) {
            println("collector started")
            flowWithInitial.collect {
                println("collected: $it")
                collected.add(it)
            }
        }

        delay(10)
        strCallback.sendItem("One")
        delay(500)
        job.cancel()

        assertEquals( listOf("Initial", "One"), collected)
    }
}
