package com.loneoaktech.components.coroutine.cache

interface CacheMap<in K: Any?, T: Any?> {

    /**
     * Returns the item for the requested key.  If the item is not currently in
     * the cache, the fetcher will be called to load the requested item.
     * Will return null only if T is a nullable type and the fetcher returned null.
     */
    suspend fun get( key: K ): T?

    /**
     * Returns the item for the requested key if it is already in the cache.
     * If not, null is returned and a fetch is not executed.
     */
    suspend fun getIfInCache(key: K): T?

    /**
     * Clear the cache. Removes all entries.
     */
    suspend fun invalidate()

    /**
     * Puts an entry into the cache at the specified key.
     */
    suspend fun put( key: K, value: T )

    suspend fun remove(key: K)
}
