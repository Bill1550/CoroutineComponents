@file:OptIn(ExperimentalStdlibApi::class)

package com.loneoaktech.components.coroutine.preferences

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.loneoaktech.components.coroutine.preferences.shared.SharedPreferencesWrapper
import com.loneoaktech.components.coroutine.preferences.shared.StringPreferenceWrapper
import com.loneoaktech.components.coroutine.preferences.simple.SimpleSharedPreferencesWrapper
import com.loneoaktech.components.coroutine.preferences.simple.SimpleStringPreferenceWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SimplePreferenceWrapperTest {

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

        val prefsWrapper = SimpleSharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            dispatcher = testDispatcher
        )

        val stringPref = SimpleStringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        stringPref.set("one")
        delay(100)
        assertEquals("one", stringPref.get() )
    }

    @Test
    fun flowGetTest() = runTest(testDispatcher) {

        val prefsWrapper = SimpleSharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            dispatcher = testDispatcher
        )

        val stringPref = SimpleStringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        stringPref.set("one")
        delay(100)
        assertEquals("one", stringPref.asFlow().first() )
    }

    /**
     * Test the flow using standard dispatchers
     */
    @Test
    fun basicFlowTest() = runBlocking(Dispatchers.Main) {
        val prefsWrapper = SimpleSharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            dispatcher = Dispatchers.Default //testDispatcher
        )

        val stringPref = SimpleStringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        val testData = listOf("one", "two", "three", "four")

        // initialize
        stringPref.set(testData[0])
        delay(100) // give time for preference to actually set

        // Only read 3 elements, last in test list should be lost
        val result = async(start = CoroutineStart.UNDISPATCHED) {
            stringPref.asFlow().take(3).toList()
        }

        delay(100) // give second coroutine time to start

        for( i in 1 until testData.size) {
            stringPref.set(testData[i])
            delay(100)
        }

        assertEquals( testData.take(3), result.await() )
    }

    @Test
    fun turbineTest() = runTest {

        val prefsWrapper = SimpleSharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            dispatcher = Dispatchers.Default //prefs should run on a different thread
        )

        val stringPref = SimpleStringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

        val testData = listOf("one", "two", "three", "four")

        // initialize
        stringPref.set(testData[0])
        delay(100) // give time for preference to actually set

        // test correct initial value
        stringPref.asFlow().test {
            assertEquals( testData[0], awaitItem())
        }

        // test a sequence
        stringPref.asFlow().test {
            // initial value
            assertEquals( testData[0], awaitItem())

            testData.drop(1).forEach { value ->
                stringPref.set(value)
                assertEquals( value, awaitItem() )
            }
        }
    }


    @Test
    fun multipleSubscriberTest() = runBlocking(testScope.coroutineContext){ //(testDispatcher) {

        val prefsWrapper = SimpleSharedPreferencesWrapper(
            prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE ),
            dispatcher = prefsScope.coroutineContext[CoroutineDispatcher.Key]!!

        )

        val stringPref = SimpleStringPreferenceWrapper(STRING_PREF_NAME, "default-value", prefsWrapper)

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
}