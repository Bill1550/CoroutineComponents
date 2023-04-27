package com.loneoaktech.components.coroutine

import kotlinx.coroutines.delay

suspend fun simpleSuspendFunction( value: Int): Int {
    delay(1000)
    return 2*value
}