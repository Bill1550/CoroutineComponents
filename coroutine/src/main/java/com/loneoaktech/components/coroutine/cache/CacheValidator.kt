package com.loneoaktech.components.coroutine.cache

interface CacheValidator<T,C> {

    fun isFresh( entry: CacheEntry<T, C>): Boolean

    /**
     * Creates the data context used to validate a normal entry.
     */
    fun createContext( entry: T) : C

    /**
     * Creates the data context used to validate an error entry.
     */
    fun createContext( t: Throwable): C
}