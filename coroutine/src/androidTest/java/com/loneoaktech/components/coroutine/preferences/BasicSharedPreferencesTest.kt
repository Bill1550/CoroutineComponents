package com.loneoaktech.components.coroutine.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class BasicSharedPreferencesTest {
    companion object {
        private const val PREFS_NAME = "test-preferences"
        private const val INT_PREF_NAME = "int1"
        private const val NOT_SET = -2
        private const val DEFAULT_VALUE = -1
    }

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * Does the shared preference share the initial state of the preference on
     * subscription.
     */
    @Test
    fun testInitialStateObservation() {

        val sp = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val observedValue = AtomicInteger(NOT_SET)

        // initialize value
        sp.edit(commit = true) { putInt(INT_PREF_NAME, 1) }

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            val value = prefs.getInt(key, DEFAULT_VALUE)
            observedValue.set(value)
            println("onChange called: key=$key, value=$value")
        }

        sp.registerOnSharedPreferenceChangeListener(listener)
        Thread.sleep(250)
        println("----> listener registered, observedValue=${observedValue.get()}")
        assertEquals(NOT_SET, observedValue.get())  // Verify that listener is not automatically called at registration.

        sp.edit(commit = true) { putInt(INT_PREF_NAME, 2)}

        Thread.sleep(250) // let queue clear
        println("Set value to 2, observed=${observedValue.get()}")
        assertEquals(2, observedValue.get())
        sp.unregisterOnSharedPreferenceChangeListener(listener)
    }

    @Test
    fun testPrefsAccessOffMainThread() = runBlocking {
        val sp = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit { putInt(INT_PREF_NAME, NOT_SET) }

        val job = launch(start = CoroutineStart.LAZY) {
            withContext(Dispatchers.Default) {
                sp.edit {
                    putInt(INT_PREF_NAME, 42)
                }
            }
        }

        // shouldn't be set yet.
        assertEquals( NOT_SET, sp.getInt(INT_PREF_NAME, NOT_SET) )

        job.join()

        println("----> Set value=${sp.getInt(INT_PREF_NAME, -1)}")
        assertEquals(42, sp.getInt(INT_PREF_NAME, -1))

        Unit
    }
}