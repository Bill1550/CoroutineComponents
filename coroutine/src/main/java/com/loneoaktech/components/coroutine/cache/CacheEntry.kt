package com.loneoaktech.components.coroutine.cache


sealed class CacheEntry<K,T,C>( val context: C ){
    class Error<K,T,C>( context: C, val error: Throwable ) : CacheEntry<K,T,C>(context)
    class Data<K,T,C>( context: C, val data: T ) : CacheEntry<K,T, C>(context)
}