package com.lumiyaviewer.lumiya.modern.protocol

class WebSocketEventClient {
    fun setAuthToken(token: String) {}
    fun connect(url: String) {}
    fun subscribe(eventType: String, listener: EventListener) {}
    fun sendRawMessage(message: String): Boolean = false
    fun isConnected(): Boolean = false
    fun shutdown() {}
    
    interface EventListener {
        fun onEvent(event: EventMessage)
    }
    
    class EventMessage(private val data: String) {
        fun getData(): String = data
    }
}
