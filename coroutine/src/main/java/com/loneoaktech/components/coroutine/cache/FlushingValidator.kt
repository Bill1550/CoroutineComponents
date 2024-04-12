package com.loneoaktech.components.coroutine.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * An experimental validator that tires to flush it's own entry
 * when it expires
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FlushingValidator<K,T>(
    private val timeToLive: Long,
    private val errorTimeout: Long,
    private val timerScope: CoroutineScope,
    private val onExpired: suspend (K)->Unit
) : CacheValidator<K,T, Job> {
    override fun isFresh(entry: CacheEntry<K,T, Job>): Boolean {
        return entry.context.isActive
    }


    override fun createContext(key: K, data: T): Job {
        return timerScope.launch(start=CoroutineStart.ATOMIC) {
            delay(timeToLive)
            onExpired(key)
        }
    }

    override fun createContext(key: K, t: Throwable): Job {
        return timerScope.launch { delay(errorTimeout)}
    }

    override suspend fun dispose(context: Job) {
        context.cancelAndJoin()
    }
}