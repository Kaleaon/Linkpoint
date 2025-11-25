package com.lumiyaviewer.lumiya.slproto.caps

import com.lumiyaviewer.lumiya.slproto.types.LLSD

class SLCapEventQueue(url: String, handler: ICapsEventHandler) {
    
    interface ICapsEventHandler {
        fun onCapsEvent(event: CapsEvent)
    }

    enum class CapsEventType {
        TeleportFinish,
        TeleportFailed,
        EstablishAgentCommunication,
        Other
    }

    data class CapsEvent(
        val eventType: CapsEventType,
        val eventName: String,
        val eventBody: LLSD
    )

    fun start() {}
    fun stop() {}
    fun stopQueue() { stop() }
}
