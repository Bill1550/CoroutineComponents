package com.loneoaktech.components.coroutine

import com.loneoaktech.components.coroutine.cache.AutonomousCacheMap
import com.loneoaktech.components.coroutine.cache.TimeValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests of the AutonomousCacheMap using the Kotlin Testing Lib.
 * That lib apparently requires all coroutines to run with the same test dispatcher, so
 * not the same as running the fetcher on a different thread.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AutonomousCacheMapTest {

    data class Stuff( val id: Int, val payload: String )

    private val testDispatcher =  StandardTestDispatcher()

    private val fetchCounter = AtomicInteger()
    private val fetchCancelCounter = AtomicInteger()
    private val fetchErrorCounter = AtomicInteger()
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

    private val fetchScope = CoroutineScope(SupervisorJob()+testDispatcher)

    private val stuffCache = AutonomousCacheMap<Int,Stuff,Long>(
            validator = TimeValidator<Int,Stuff>( TTL, 10 ){ testDispatcher.scheduler.currentTime },
            fetcher = ::fetchStuff,
            fetchingScope = fetchScope
    )

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
        assertEquals("Fetch counter should be 1", 1, fetchCounter.get() )

        val s1a = stuffCache.get(1)
        assertEquals("Cache contents not re-used", 1, fetchCounter.get() )
        assertTrue("Responses were not same object, should have been", s1 === s1a )
    }

    @Test
    fun `verify basic TTL`() = runTest {
        val s1 = stuffCache.get(1)
        println("stuff 1 = $s1")
        delay(TTL/2)
        assertNotNull("should still be valid", stuffCache.getIfInCache(1))
        delay(TTL/2+1)
        assertNull("Should not have expired", stuffCache.getIfInCache(1))
    }

    @Test
    fun multiJobTest() = runTest(testDispatcher) {

        var s1: Stuff? = null
        var s1a: Stuff? = null

        launch {
            s1 = stuffCache.get(1)
            assertEquals("Fetch counter should be 1", 1, fetchCounter.get() )
        }

        delay(10) // ensure j2 starts after j1

        // Because the fetcher has a 200 ms delay, J1 should still be in the fetcher delay
        // and this job should wait for it to finish, thus only one fetch should happen.

        launch {
            s1a = stuffCache.get(1)
            assertEquals("Fetch counter should not have incremented", 1, fetchCounter.get() )
        }

        advanceUntilIdle()
        assertTrue( s1 === s1a )
    }

    @Test
    fun cancelFirstJobTest() = runTest(testDispatcher) {

        var s1: Stuff? = null
        var s1a: Stuff? = null

        val j1 = launch {
            s1 = stuffCache.get(1)
            assertFalse("job should have been canceled before this", true)
        }

        delay(10) // ensure j2 starts after j2

        val j2 = launch {
            s1a = stuffCache.get(1)
            assertEquals("Fetch counter should be 1", 1, fetchCounter.get() )
        }

        delay(10) // ensure both jobs are started and at mutex

        assertTrue( isInSimulatedFetch.get() )
        j1.cancel()

        joinAll(j1,j2)
        assertEquals( 1, s1a?.id )
        assertEquals( 1, fetchCounter.get() )
        assertEquals( 0, fetchCancelCounter.get() )
    }

    @Test
    fun ioErrorTest() = runTest(testDispatcher) {

        var s1: Stuff? = null
        var e1: Exception? = null


        fun CoroutineScope.read1(): Job {
            return launch {
                s1 = null
                e1 = null
                try {
                    s1 = stuffCache.get(1)
                } catch ( e: IOException ) {
                    e1 = e
                }
            }
        }

        throwFetchError.set(true)

        val j1 = read1()

        j1.join()
        assertNotNull( e1 )
        assertNull( s1 )

        delay(50)   // get past exception timeout
        val j2 = read1()
        j2.join()
        assertNull( e1 )
        assertNotNull( s1 )
    }

    @Test
    fun ioErrorMultiCallerTest() = runTest(testDispatcher) {

        var s1: Stuff? = null
        var e1: Exception? = null

        fun CoroutineScope.read1(): Job {
            return launch {
                s1 = null
                e1 = null
                try {
                    s1 = stuffCache.get(1)
                } catch ( e: IOException ) {
                    e1 = e
                }
            }
        }

        var s2: Stuff? = null
        var e2: Exception? = null


        fun CoroutineScope.read2(): Job {
            return launch {
                s2 = null
                e2 = null
                try {
                    s2 = stuffCache.get(1)
                } catch ( e: IOException ) {
                    e2 = e
                }
            }
        }

        throwFetchError.set(true)
        val j1 = read1()
        delay(20)       // make sure 2 starts read after 1 starts
        val j2 = read2()

        joinAll(j1,j2)
        assertNotNull( e1 )
        assertNotNull( e2 )
        assertEquals(1, fetchErrorCounter.get() )
    }

    @Test
    fun clearTest() = runTest(testDispatcher) {

        val s1 = stuffCache.get(1)
        println("stuff 1 = $s1")
        assertEquals("Fetch counter should be 1", 1, fetchCounter.get() )

        stuffCache.invalidate()
        val s1a = stuffCache.get(1)
        assertNotEquals("Cache contents was reused, clear failed", 1, fetchCounter.get() )
        assertEquals("fetch should have been called twice", 2, fetchCounter.get() )
        assertFalse("Responses were same object, should not have been", s1 === s1a )
    }

    @Test
    fun `verify get if present`() = runTest {

        // load item into cache
        val s1 = stuffCache.get(1)
        println("stuff 1 = $s1")
        assertEquals(1, stuffCache.getInclusiveSize())
        // wait for it to expire
        delay(TTL*2)

        assertNull("Should no longer be valid", stuffCache.getIfInCache(1))
        assertEquals("cache entrires should have been cleared", 0, stuffCache.getInclusiveSize())
    }

    @Test
    fun `verify getSize`() = runTest {
        val s1 = stuffCache.get(1)
        println("stuff 1 = $s1")
        assertEquals("Item should still be in cache", 1, stuffCache.getSize())
        delay(TTL/2)
        val s2 = stuffCache.get(2)
        assertEquals("Both items should still be fresh", 2, stuffCache.getSize())
        delay(TTL/2+10)
        assertEquals("One item should be expired and cleared out", 1, stuffCache.getSize())
        delay(TTL)
        assertEquals("Both items should have timed out and cache should be clear", 0, stuffCache.getSize())
    }
}