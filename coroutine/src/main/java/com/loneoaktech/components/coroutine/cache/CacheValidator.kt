package com.loneoaktech.components.coroutine.cache

interface CacheValidator<K, T, C> {

    fun isFresh(entry: CacheEntry<K, T, C>): Boolean

    /**
     * Creates the data context used to validate a normal entry.
     */
    fun createContext(key: K, data: T): C

    /**
     * Creates the data context used to validate an error entry.
     */
    fun createContext(key: K, t: Throwable): C

    suspend fun dispose(context: C) {}
}