package com.loneoaktech.components.coroutine.cache

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * A coroutine based cache that uses a separate CoroutineScope
 * to run the fetching function.  This allows the fetcher to complete
 * even if a requester is cancelled.
 * If additional coroutines request an item while the fetcher is running
 * from the initial request, the fetcher is not relaunched even if the
 * initiating coroutine is cancelled.
 *
 * Supports null cache entries if the fetcher returns a nullable type.
 *
 * A CacheValidator object is used to determine if a cache entry is
 * still fresh.  The validator is called when the entry is first created
 * to supply an entry context where the validator can save load time, etc.
 * When an entry is fetched the validator is asked to determine freshness.
 * This allows the TTL calculation to be item dependent.
 *
 * The cache serializes all fetches even for different keys, so only
 * one fetch is active at a time. However, a caller requesting a value
 * for a different key doesn't have to wait if the cache contents for
 * that key exist and are valid.
 *
 * Type parameters:
 *  K - map key
 *  T - content type
 *  C - validation context
 */
class AutonomousCacheMap<in K : Any?, T : Any?, C : Any>(

    /**
     * An instance of CacheValidator that determines if an item
     * in the cache can still be used.
     * The TimeValidator concrete instance provides simple TTL validation.
     */
    private val validator: CacheValidator<K,T,C>,

    private val fetchingScope: CoroutineScope,

    private val fetcher: suspend (K) -> T

) : CacheMap<K, T> {

    private val map = mutableMapOf<K, CacheEntry<K,T, C>>()
    private val mutex = Mutex()
    private var fetchingJob: Job? = null


    /**
     * Returns the item for the requested key.  If the item is not currently in
     * the cache, the fetcher will be called to load the requested item.
     * Will return null only if T is a nullable type and the fetcher returned null.
     */
    override suspend fun get(key: K): T? {

        // Initially look in cache w/o locking
        return (map[key]?.takeIf { validator.isFresh(it) } ?: let {
            // Not in cache, or stale, need to do a fetch (maybe)
            mutex.lock()

            // check cache again (double check cache locking), in case entry arrived while we were waiting for mutex
            map[key]?.takeIf { validator.isFresh(it) }?.let {
                // did appear while locking
                mutex.unlock()
                it // return what we got
            } ?: let {
                // now do the fetch
                map.remove(key)?.let {validator.dispose(it.context)} // make sure cache entry is clear (in case it was stale)

                // Go get a new entry, running in a separate coroutine scope
                fetchingJob = fetchingScope.launch {
                    try {
                        map[key] = fetcher(key).let { CacheEntry.Data(validator.createContext(key,it), it) }
                    } catch (ce: CancellationException) {
                        // don't store cancellation exceptions.
                    } catch (t: Throwable) {
                        map[key] = CacheEntry.Error(validator.createContext(key,t), t)
                    } finally {
                        mutex.unlock() // make sure mutex is always unlocked when we finish I/O
                    }
                }.apply { join() } // wait for the fetching job to complete
                map[key] // return the entry that the fetching job found
            }
        }).let { entry ->
            if (entry == null)
                throw CancellationException("entry null")

            entry.dispatch()
        }

    }

    /**
     * Returns the item for the requested key if it is already in the cache.
     * If not, null is returned and a fetch is not executed.
     */
    override suspend fun getIfInCache(key: K): T? {
        return map[key]?.takeIf { validator.isFresh(it) }?.dispatch() ?: let {
            mutex.withLock {
                // clear cache entry, to free up memory, if it didn't suddenly get
                // renewed.
                if (map[key]?.takeIf { validator.isFresh(it) } == null) {
                    map.remove(key)?.let {validator.dispose(it.context)}
                }
            }
            null
        }
    }

    /**
     * Puts an entry into the cache at the specified key.
     * (side load)
     */
    override suspend fun put(key: K, value: T) {
        return mutex.withLock {
            map.remove(key)?.let {validator.dispose(it.context)}
            map[key] = CacheEntry.Data(validator.createContext(key,value), value)
        }
    }

    override suspend fun remove(key: K ) {
        mutex.withLock {
            map.remove(key)?.let {validator.dispose(it.context)}
        }
    }

    /**
     * Clear the cache. Removes all entries.
     */
    override suspend fun invalidate() {
        fetchingJob?.cancelAndJoin()
        mutex.withLock {
            map.forEach { (_, cacheEntry) -> validator.dispose(cacheEntry.context) }
            map.clear()
        }
    }



    /**
     * Dispatches either the cached value or the cached error.
     */
    @Suppress("UNCHECKED_CAST")
    private fun CacheEntry<K,T,C>.dispatch(): T {
        return when (this) {
            is CacheEntry.Data<*,*,*> -> data as T
            is CacheEntry.Error -> throw error
        }
    }


    /**
     * Gets the number of items in the cache, even if they have expired.
     */
    @VisibleForTesting
    fun getInclusiveSize(): Int = map.size


    /**
     * Returns the number of fresh entries in the cache.
     * This will test every member, and clear any that are expired.
     * It purges unused memory, but at the cost of O(n)
     */
    suspend fun getSize(): Int =
        mutex.withLock {
            if (map.isEmpty())
                return@withLock 0
            map.keys.toSet().forEach { key ->
                if (map[key]?.takeIf { validator.isFresh(it) } == null) {
                    map.remove(key)?.let {validator.dispose(it.context)}
                }
            }

            map.size
        }

}