package com.linkpoint.utils.reqset

interface RequestCompleteListener<T> {
    fun onRequestComplete(item: T)
}