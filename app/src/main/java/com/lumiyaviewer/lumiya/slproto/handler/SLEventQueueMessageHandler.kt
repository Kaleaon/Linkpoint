package com.lumiyaviewer.lumiya.slproto.handler

import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SLEventQueueMessageHandler(
    val eventName: SLCapEventQueue.CapsEventType
)