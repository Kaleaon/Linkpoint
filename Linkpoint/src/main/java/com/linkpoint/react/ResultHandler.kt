package com.linkpoint.react

interface ResultHandler<K, T> {
    fun onResultData(request: K, data: T)
    fun onResultError(request: K, error: Throwable)
}