package com.loneoaktech.components.coroutine

import com.loneoaktech.components.coroutine.cache.AutonomousCacheMap
import com.loneoaktech.components.coroutine.cache.FlushingValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class FlushingValidatorTest {
    data class Stuff( val id: Int, val payload: String )

    private val testDispatcher =  StandardTestDispatcher()

    private val fetchCounter = AtomicInteger()
    private val fetchCancelCounter = AtomicInteger()
    private val fetchErrorCounter = AtomicInteger()
    private val flushCounter = AtomicInteger()
    private val throwFetchError = AtomicBoolean()
    private val isInSimulatedFetch = AtomicBoolean()

    private val TTL = 500L
    private val FETCH_TIME = 200L

    private suspend fun fetchStuff( key: Int ): Stuff {
        return try {
            withContext( testDispatcher ){
                isInSimulatedFetch.set(true)
                delay(FETCH_TIME)
                isInSimulatedFetch.set(false)
                if ( throwFetchError.getAndSet(false) ) {
                    println("fetcher throwing io exception")
                    fetchErrorCounter.incrementAndGet()
                    throw IOException("fake IO exception")
                }

                fetchCounter.incrementAndGet()
                println("fetcher return stuff for $key")
                Stuff(key, "Stuff for $key")
            }
        } catch (ce: CancellationException) {
            fetchCancelCounter.incrementAndGet()
            throw ce
        }
    }


    private val fetchScope = CoroutineScope(SupervisorJob() + testDispatcher)


    // This is a KLUDGE
    private lateinit var flusher: suspend (Int)->Unit

    private val stuffCache = AutonomousCacheMap(
        validator = FlushingValidator(
            timeToLive = TTL,
            errorTimeout = 10,
            timerScope = fetchScope,
            onExpired = {flusher(it)} // call in a second lambda o allow the lateinit to initalize
        ),
        fetcher = ::fetchStuff,
        fetchingScope = fetchScope
    )

    init {
        flusher = {key ->
            flushCounter.incrementAndGet()
            fetchScope.launch {
                // causes cache entry to be cleared if still timed out.
                stuffCache.getIfInCache(key)
            }
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
//        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun simpleTest() = runTest(testDispatcher) {

        val s1 = stuffCache.get(1)
        println("stuff 1 = $s1")
        Assert.assertEquals("Fetch counter should be 1", 1, fetchCounter.get() )

        val s1a = stuffCache.get(1)
        Assert.assertEquals("Cache contents not re-used", 1, fetchCounter.get() )
        Assert.assertTrue("Responses were not same object, should have been", s1 === s1a )
    }

    @Test
    fun `verify basic TTL`() = runTest {
        val s1 = stuffCache.get(1)
        println("stuff 1 = $s1")
        Assert.assertEquals("Fetch counter should be 1", 1, fetchCounter.get() )

        delay(TTL/2)
        Assert.assertNotNull("should still be valid", stuffCache.getIfInCache(1))

        delay(TTL)
        val s1a = stuffCache.get(1)
        Assert.assertEquals("Contents were not re-fetched", 2, fetchCounter.get() )
        Assert.assertTrue("Responses should not be the same object", s1 !== s1a )
        delay(2*TTL)    // let all pending timeouts expire
    }

    @Test
    fun `verify that cache flushes`() = runTest {
        val s1 = stuffCache.get(1)
        delay(TTL/2)
        Assert.assertEquals("The single item should still be in cache", 1, stuffCache.getInclusiveSize())

        delay(TTL)
        Assert.assertEquals("Cache entry should have deleted itself", 0, stuffCache.getInclusiveSize())
        Assert.assertEquals("Should have been only 1 flush", 1, flushCounter.get())
    }

    @Test
    fun `verify timeout edge case`() = runTest {
        val s1 = stuffCache.get(1)
        delay(TTL-1)
        val s1a = stuffCache.get(1)
        Assert.assertEquals("Should not have done second fetch", 1, fetchCounter.get() )

        delay(TTL*2)    // let cache clear
        Assert.assertEquals("Cache should have flushed", 0, stuffCache.getInclusiveSize())
        Assert.assertEquals("The single item should have been flushwed", 1, flushCounter.get())

        val s2 = stuffCache.get(2)
        delay(TTL)
        val s2a = stuffCache.get(2)
        Assert.assertEquals("Should have made a 3rd fetch", 3, fetchCounter.get())
        Assert.assertEquals("The first try at #2 should have been flushed", 2, flushCounter.get())
    }

}