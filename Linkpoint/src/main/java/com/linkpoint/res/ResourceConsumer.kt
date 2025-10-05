package com.linkpoint.res

interface ResourceConsumer {
    fun OnResourceReady(obj: Any?, ready: Boolean)
}