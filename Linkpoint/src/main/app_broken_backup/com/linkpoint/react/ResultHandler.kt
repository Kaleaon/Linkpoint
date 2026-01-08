package com.linkpoint.react

interface ResultHandler<K, T> {
    fun onResultData(
        key: K,
        data: T,
    )

    fun onResultError(
        key: K,
        error: Throwable,
    )
}
