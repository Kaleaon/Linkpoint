package com.linkpoint.slproto.handler

import com.linkpoint.slproto.caps.SLCapEventQueue

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SLEventQueueMessageHandler(
    val eventName: SLCapEventQueue.CapsEventType
)