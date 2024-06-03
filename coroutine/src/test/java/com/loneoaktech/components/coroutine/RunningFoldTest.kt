package com.loneoaktech.components.coroutine

import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.plus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

/**
 * Experiments to measure the time difference between using a runningFold with a
 * mutable or immutable map.
 * NOTE: the documentation on runningFold specifies that the accumulator should be immutable
 * so using a mutable map is a really bad idea if the map is ever touched anywhere else.
 *
 * When running these tests one at a time, the time advantage for the mutable map appears to be
 * about 2x. However when running the tests all together, it is about 20x.
 * The last test runs all the samples in one test to isolate the effect of how the test framework
 * re initializes the containing class between runs.
 *
 */
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class RunningFoldTest {

    @JvmInline
    value class Id(val value: String)

    data class TestItem(
        val id: Id,
        val timeCreated: Long
    )

    private fun testSource(size: Int) = (0 until size).asFlow()
        .map { Id(it.toString()) }
        .map {
            TestItem(
                id = it,
                timeCreated = System.currentTimeMillis()
            )
        }

    private val testSize = 1000

    private suspend fun kotlinImmutableMapTest(testSize: Int): TimedValue<Map<Id, TestItem>> =
        measureTimedValue {
            testSource(testSize)
                .runningFold(persistentMapOf<Id, TestItem>()) { acc, item ->
                    acc.plus(item.id to item)
                }
                .last()
        }

    private suspend fun immutableMapTest(testSize: Int): TimedValue<Map<Id, TestItem>> =
        measureTimedValue {
            testSource(testSize)
                .runningFold(emptyMap<Id, TestItem>()) { acc, item ->
                    acc + (item.id to item)
                }
                .last()
        }

    @Test
    fun foldImmutableMapTest() = runTest {
        val result = immutableMapTest(testSize)
        assertEquals(testSize, result.value.size)
        println("Immutable map Run time = ${result.duration}")
    }

    @Test
    fun foldKotlinImmutableMapTest() = runTest {
        val result = kotlinImmutableMapTest(testSize)
        assertEquals(testSize, result.value.size)
        println("Immutable map Run time = ${result.duration}")
    }


    /**
     * Test using a mutable map as the accumulator.
     * This violates the documentation for runningFold which specifies an
     * immutable initial accumulator
     */
    private suspend fun mutableMapTest(testSize: Int): TimedValue<Map<Id, TestItem>> =
        measureTimedValue {
            testSource(testSize)
                .runningFold(mutableMapOf<Id, TestItem>()) { acc, item ->
                    acc.put(item.id, item)
                    acc
                }
                .last()
        }

    @Test
    fun foldMutableMapTest() = runTest {
        val result = mutableMapTest(testSize)
        println("Mutable map Run time = ${result.duration}")
        assertEquals(testSize, result.value.size)
    }

    /**
     * An ateempt to create a fold (in this case a reduce because the accumulator is contained)
     * that keeps the mutable map private.
     *
     * IT turns out that copying the mutable map to an immutable map is the big time sink.
     */
    private fun <K, V> Flow<V>.manualRunningMapReduce(by: (V) -> K): Flow<Map<K, V>> = flow {
        val workingMap = mutableMapOf<K, V>()
        this@manualRunningMapReduce.collect { item ->
            val key = by(item)
            workingMap[key] = item
            emit(workingMap.toMap())
        }
    }

    private suspend fun manualRunningReduceTest(testSize: Int): TimedValue<Map<Id, TestItem>> =
        measureTimedValue {
            testSource(testSize)
                .manualRunningMapReduce { item -> item.id }
                .last()
        }

    private fun <K, V> Flow<V>.kotlinManualRunningMapReduce(by: (V) -> K): Flow<Map<K, V>> = flow {
        var workingMap = persistentMapOf<K, V>()
        this@kotlinManualRunningMapReduce.collect { item ->
            val key = by(item)
            workingMap = workingMap.plus (key to item)
            emit(workingMap)
        }
    }

    private suspend fun kotlinManualRunningReduceTest(testSize: Int): TimedValue<Map<Id, TestItem>> =
        measureTimedValue {
            testSource(testSize)
                .kotlinManualRunningMapReduce { item -> item.id }
                .last()
        }


    @Test
    fun manualRunningReduceTest() = runTest {
        val result = manualRunningReduceTest(testSize)
        println("Manual Run time = ${result.duration}")
        assertEquals(testSize, result.value.size)
    }

    /**
     * Like the manualRunningMapReduce, but emits the mutable map.
     */
    private fun <K, V> Flow<V>.dangerousManualRunningMapReduce(by: (V) -> K): Flow<Map<K, V>> =
        flow {
            val workingMap = mutableMapOf<K, V>()
            this@dangerousManualRunningMapReduce.collect { item ->
                val key = by(item)
                workingMap[key] = item
                emit(workingMap)
            }
        }

    private suspend fun dangerousManualRunningReduceTest(testSize: Int): TimedValue<Map<Id, TestItem>> =
        measureTimedValue {
            testSource(testSize)
                .dangerousManualRunningMapReduce { item -> item.id }
                .last()
        }

    @Test
    fun dangerousManualRunningReduceTest() = runTest {
        val result = dangerousManualRunningReduceTest(testSize)
        println("Dangerous manual Run time = ${result.duration}")
        assertEquals(testSize, result.value.size)
    }

    @Test
    fun runAllTestsTogether() = runTest {
        println("All tests together")

        val result1 = immutableMapTest(testSize)
        assertEquals(testSize, result1.value.size)
        println("Immutable map Run time = ${result1.duration}")

        val result2 = mutableMapTest(testSize)
        println("Mutable map Run time = ${result2.duration}")
        assertEquals(testSize, result2.value.size)

        val result3 = kotlinImmutableMapTest(testSize)
        assertEquals(testSize, result3.value.size)
        println("Kotlin immutable Map run time = ${result3.duration}")

        val result4 = manualRunningReduceTest(testSize)
        println("Manual Run time = ${result4.duration}")
        assertEquals(testSize, result4.value.size)

        val result5 = dangerousManualRunningReduceTest(testSize)
        println("Dangerous manual Run time = ${result5.duration}")
        assertEquals(testSize, result5.value.size)

        val result6 = kotlinManualRunningReduceTest(testSize)
        println("Kotlin manual run time = ${result6.duration}")
        assertEquals(testSize, result6.value.size)
    }
}
