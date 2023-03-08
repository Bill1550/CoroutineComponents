package com.loneoaktech.components.coroutine.cache


sealed class CacheEntry<T,C>( val context: C ){
    class Error<T,C>( context: C, val error: Throwable ) : CacheEntry<T, C>(context)
    class Data<T,C>( context: C, val data: T ) : CacheEntry<T, C>(context)
}