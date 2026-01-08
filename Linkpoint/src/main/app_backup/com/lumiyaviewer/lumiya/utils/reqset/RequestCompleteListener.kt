package com.lumiyaviewer.lumiya.utils.reqset

interface RequestCompleteListener<T> {
    fun onRequestComplete(result: T)
}
