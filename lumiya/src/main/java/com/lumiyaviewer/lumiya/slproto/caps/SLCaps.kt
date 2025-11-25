package com.lumiyaviewer.lumiya.slproto.caps

import java.util.HashMap

class SLCaps {
    class NoSuchCapabilityException : Exception()

    enum class SLCapability {
        EventQueueGet
    }

    fun GetCapabilites(loginURL: String, seedCapability: String) {
        // Stub
    }

    @Throws(NoSuchCapabilityException::class)
    fun getCapabilityOrThrow(capability: SLCapability): String {
        return ""
    }
}
