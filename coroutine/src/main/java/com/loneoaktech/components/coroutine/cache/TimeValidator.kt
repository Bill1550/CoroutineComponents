package com.loneoaktech.components.coroutine.cache

/**
 * A basic TTL validator for the ContextCacheMap.
 */
open class TimeValidator<K,T>(
        private val timeToLive: Long,
        private val errorTimeout: Long,
        private val currentTimeSource: ()->Long = {System.currentTimeMillis()}
) : CacheValidator<K,T, Long> {

    override fun createContext(key: K, data: T): Long {
        return currentTimeSource()
    }

    override fun createContext(key: K, t: Throwable): Long {
        return currentTimeSource()
    }

    override fun isFresh(entry: CacheEntry<K,T, Long>): Boolean {
        return when ( entry ) {
            is CacheEntry.Data<*,*,*> -> currentTimeSource() - entry.context < timeToLive
            is CacheEntry.Error<*,*,*> -> currentTimeSource() - entry.context < errorTimeout
        }
    }

}