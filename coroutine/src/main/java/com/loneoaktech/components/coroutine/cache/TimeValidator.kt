package com.loneoaktech.components.coroutine.cache

/**
 * A basic TTL validator for the ContextCacheMap.
 *
 *
 */
open class TimeValidator<T>(
        private val timeToLive: Long,
        private val errorTimeout: Long,
        private val currentTimeSource: ()->Long = {System.currentTimeMillis()}
) : CacheValidator<T, Long> {

//    constructor (
//        timeToLive: Duration,
//        errorTimeout: Duration,
//        currentTimeSource: ()->Long = {System.currentTimeMillis()}
//        ) : this( timeToLive.toLongMilliseconds(), errorTimeout.toLongMilliseconds(), currentTimeSource )

    override fun createContext(entry: T): Long {
        return currentTimeSource()
    }

    override fun createContext(t: Throwable): Long {
        return currentTimeSource()
    }

    override fun isFresh(entry: CacheEntry<T, Long>): Boolean {
        return when ( entry ) {
            is CacheEntry.Data<*,*> -> currentTimeSource() - entry.context < timeToLive
            is CacheEntry.Error<*,*> -> currentTimeSource() - entry.context < errorTimeout
        }
    }

}