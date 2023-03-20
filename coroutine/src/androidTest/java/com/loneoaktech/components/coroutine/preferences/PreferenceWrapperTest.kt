package com.loneoaktech.components.coroutine.preferences

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.loneoaktech.components.coroutine.preferences.shared.PreferenceWrapper
import com.loneoaktech.components.coroutine.preferences.shared.SharedPreferencesWrapper
import com.loneoaktech.components.coroutine.preferences.shared.StringPreferenceWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PreferenceWrapperTest {

    companion object {
        private const val PREFS_NAME = "test-preferences"
        private const val INT_PREF_NAME = "int1"
        private const val STRING_PREF_NAME = "str1"
        private const val NOT_SET = -2
        private const val DEFAULT_VALUE = -1
    }

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(DelicateCoroutinesApi::class)
    private val prefsScope = CoroutineScope(newSingleThreadContext("PrefsContext"))
    lateinit var testScope: CoroutineScope

    @Before
    fun initializePrefs() {
        appContext.deleteSharedPreferences(PREFS_NAME)
        testScope = CoroutineScope( SupervisorJob() + Dispatchers.Main )
    }

    @After
    fun cleanupPrefs()  {
        appContext.deleteSharedPreferences(PREFS_NAME)
        testScope.cancel()
    }

    @Test
    fun basicWrapperTest() = runTest(testDispatcher) {

        val prefsWrapper = SharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            scope = backgroundScope
        )

        val stringPref = StringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        stringPref.set("one")
        delay(100)
        val v = stringPref.asFlow().first()
        println("read value=$v")
        assertEquals("one", v )
    }

    /**
     * Test the flow using standard dispatchers
     */
    @Test
    fun basicFlowTest() = runBlocking(testScope.coroutineContext) { //runTest(testDispatcher) {
        val prefsWrapper = SharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            scope = prefsScope

        )

        val stringPref = StringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        val testData = listOf("one", "two", "three", "four")

        // initialize
        stringPref.set(testData[0])
        delay(100) // give time for preference to actually set

        // Only read 3 elements, last in test list should be lost
        val resultJob = async(start = CoroutineStart.UNDISPATCHED) {
            stringPref.asFlow().take(3).toList()
//            val results = mutableListOf<String>()
//
//            stringPref.asFlow().take(3).collect {
//                println("collected $it")
//                results.add(it ?: "-null-")
//            }
//
//            results
        }

        delay(100) // give second coroutine time to start

        for( i in 1 until testData.size) {
            stringPref.set(testData[i])
            delay(200)
        }

        val result = resultJob.await()
        println("result=$result")
        assertEquals( testData.take(3), result )
    }

    /**
     * Same as basicFlowTest but using the test library.
     * Gave inconsistent results using the StandardTestDispatcher()
     */
    @Test
    fun testLibFlowTest() = runBlocking(testScope.coroutineContext) { //runTest(testDispatcher) {
        val prefsWrapper = SharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            scope = prefsScope
        )

        val stringPref = StringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        val testData = listOf("one", "two", "three", "four")

        // initialize
        stringPref.set(testData[0])
        delay(100) // give time for preference to actually set


        val resultJob = async(start = CoroutineStart.UNDISPATCHED) {
            stringPref.asFlow().take(3).toList()
//            val results = mutableListOf<String>()
//
//            stringPref.asFlow().take(3).collect {
//                println("collected $it")
//                results.add(it ?: "-null-")
//            }
//
//            results
        }

        delay(100) // give second coroutine time to start

        for( i in 1 until testData.size) {
            stringPref.set(testData[i])
            delay(200)
        }

        val result = resultJob.await()
        println("result=$result")
        assertEquals( testData.take(3), result )
    }

    @Test
    fun turbineTest() = runBlocking(testScope.coroutineContext) { //runTest(testDispatcher){

        val prefsWrapper = SharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            scope = prefsScope //backgroundScope
        )

        val stringPref = StringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        val testData = listOf("one", "two", "three", "four")

        // initialize
        stringPref.set(testData[0])
        delay(100) // give time for preference to actually set

        // test correct initial value
        stringPref.asFlow().test {
            assertEquals( testData[0], awaitItem())

            testData.drop(1).forEach { value ->
                stringPref.set(value)
                assertEquals( value, awaitItem() )
            }
        }
    }

    @Test
    fun multipleSubscriberTest() = runBlocking(testScope.coroutineContext) {

        val prefsWrapper = SharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            scope = prefsScope
        )

        val stringPref = StringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        val testData = listOf("one", "two", "three", "four")

        // initialize
        stringPref.set(testData[0])
        delay(100) // give time for preference to actually set


        // Only read 3 elements, last in test list should be lost
        val result1Job = async(start = CoroutineStart.UNDISPATCHED) {
            stringPref.asFlow().take(3).toList()
        }

        val result2Job = async(start = CoroutineStart.UNDISPATCHED) {
            stringPref.asFlow().take(3).toList()
        }

        delay(100) // give second coroutine time to start

        for( i in 1 until testData.size) {
            stringPref.set(testData[i])
            delay(100)
        }

        val result1 = result1Job.await()
        val result2 = result2Job.await()
        println("result1=$result1")
        println("result2=$result2")
        assertEquals( testData.take(3), result1 )
        assertEquals( testData.take(3), result2 )
    }

    @Test
    fun sequentialSubscriberTest() = runBlocking(testScope.coroutineContext) {
        val prefsWrapper = SharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            scope = prefsScope
        )

        val stringPref = StringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        val testData = listOf("one", "two", "three", "four")


        // initialize
        stringPref.set(testData[0])
        delay(100) // give time for preference to actually set


        // test a sequence
        stringPref.asFlow().test {
            // initial value
            assertEquals( testData[0], awaitItem())

            testData.drop(1).forEach { value ->
                stringPref.set(value)
                assertEquals( value, awaitItem() )
            }
        }

        // A short delay, should not resubscribe
        delay( PreferenceWrapper.SUBSCRIBER_TIMEOUT/2)

        // subscribe again
        stringPref.asFlow().test {
            testData.forEach { value ->
                stringPref.set(value)
                assertEquals( value, awaitItem() )
            }
        }

        // A long delay, should resubscribe
        delay( PreferenceWrapper.SUBSCRIBER_TIMEOUT * 2)

        stringPref.asFlow().test {
            testData.forEach { value ->
                stringPref.set(value)
                // On first pass, turbine doesn't call the flow until after the set,
                // so this loop does not see inital value
                assertEquals( value, awaitItem() )
            }
        }
    }
}