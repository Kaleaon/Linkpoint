package com.linkpoint.utils.reqset

interface RequestCompleteListener<T> {
    fun onRequestComplete(result: T)
}
