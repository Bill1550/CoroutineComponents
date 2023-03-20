package com.loneoaktech.components.coroutine.preferences.simple

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


abstract class  SimplePreferenceWrapper<T> internal constructor(
    val key: String,
    val default: T,
    private val prefsWrapper: SimpleSharedPreferencesWrapper,
    private val getter: SharedPreferences.()->T,
    private val setter: SharedPreferences.Editor.(T)->Unit
) {
    suspend fun set(value: T) {
        withContext(prefsWrapper.dispatcher) {
            prefsWrapper.mutationMutex.withLock {
                prefsWrapper.prefs.edit(commit = true) {
                    setter(value)
                }
            }
        }
    }

    // This should probably be eliminated, as it could cause synchronization issues
    // with flow version.
    suspend fun get(): T {
        return withContext(prefsWrapper.dispatcher) {
            prefsWrapper.prefs.getter()
        }
    }

    fun asFlow(): Flow<T> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
            val newValue = prefsWrapper.prefs.getter()
            trySendBlocking( newValue )
        }

        // Wrap the sending of the first value in a mutex to prevent
        // flow missing a change that occurs during initialization.
        prefsWrapper.mutationMutex.withLock {
            // send initial value
            send( prefsWrapper.prefs.getter() )
            prefsWrapper.prefs.registerOnSharedPreferenceChangeListener(listener)
        }

        awaitClose {
            prefsWrapper.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.flowOn(prefsWrapper.dispatcher).conflate() // SP are by definition conflated. (?)

}

class SimpleStringPreferenceWrapper(
    key: String,
    default: String?,
    wrapper: SimpleSharedPreferencesWrapper
) : SimplePreferenceWrapper<String?>(
    key = key,
    default = default,
    prefsWrapper = wrapper,
    getter = { getString(key, default)},
    setter = { v -> putString(key, v)}
)