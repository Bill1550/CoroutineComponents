package com.loneoaktech.components.coroutine.preferences.shared

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit
import com.loneoaktech.components.coroutine.preferences.simple.SimplePreferenceWrapper
import com.loneoaktech.components.coroutine.preferences.simple.SimpleSharedPreferencesWrapper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

abstract class PreferenceWrapper<T> internal constructor(
    val key: String,
    val default: T,
    private val prefsWrapper: SharedPreferencesWrapper,
    private val getter: SharedPreferences.(String)->T,
    private val setter: SharedPreferences.Editor.(String,T)->Unit
) {
    companion object {
        @VisibleForTesting
        const val SUBSCRIBER_TIMEOUT = 500L
    }

    suspend fun set(value: T) {
        withContext(prefsWrapper.scope.coroutineContext) {
            prefsWrapper.mutationMutex.withLock {
                prefsWrapper.prefs.edit(commit = true) {
                    setter(key,value)
                }
                println("sending $value")
            }
        }
    }


    private val sf = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, k ->
            if ( k == key) {
                println("value change for key=$k")
                val newValue = sp.getter(key)
                trySendBlocking(newValue)
            }
        }

        // Wrap the sending of the first value in a mutex to prevent
        // flow missing a change that occurs during initialization.
        prefsWrapper.mutationMutex.withLock {
            // send initial value
            val curValue = prefsWrapper.prefs.getter(key)
            println("subscribing, initial value=$curValue")
            send( curValue )
            prefsWrapper.prefs.registerOnSharedPreferenceChangeListener(listener)
        }

        awaitClose {
            prefsWrapper.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.shareIn(prefsWrapper.scope, SharingStarted.WhileSubscribed(SUBSCRIBER_TIMEOUT))

    fun asFlow(): SharedFlow<T> = sf

}


class StringPreferenceWrapper(
    key: String,
    default: String?,
    wrapper: SharedPreferencesWrapper
) : PreferenceWrapper<String?>(
    key = key,
    default = default,
    prefsWrapper = wrapper,
    getter = { k -> getString(k, default)},
    setter = { k, v -> putString(k, v)}
)
