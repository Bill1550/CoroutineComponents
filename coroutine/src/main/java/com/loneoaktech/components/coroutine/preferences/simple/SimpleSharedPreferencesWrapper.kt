package com.loneoaktech.components.coroutine.preferences.simple

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex

class SimpleSharedPreferencesWrapper(
    internal val prefs: SharedPreferences,
    internal val dispatcher: CoroutineDispatcher
) {


    internal val mutationMutex = Mutex()


}