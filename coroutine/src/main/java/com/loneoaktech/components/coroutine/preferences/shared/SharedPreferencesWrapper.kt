package com.loneoaktech.components.coroutine.preferences.shared

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex

class SharedPreferencesWrapper(
    val prefs: SharedPreferences,
    val scope: CoroutineScope
) {

    internal val mutationMutex = Mutex()




}




fun Context.getWrappedPreferences(
        name: String,
        mode: Int = Context.MODE_PRIVATE,
        scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
): SharedPreferencesWrapper = SharedPreferencesWrapper(
    prefs = getSharedPreferences(name, mode ),
    scope = scope
)
