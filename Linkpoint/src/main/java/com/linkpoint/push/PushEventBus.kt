package com.linkpoint.push

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object PushEventBus {
    private val _events = MutableSharedFlow<PushEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<PushEvent> = _events

    fun publish(event: PushEvent) {
        _events.tryEmit(event)
    }
}
