package com.linkpoint.slproto

interface SLMessageEventListener {
    fun onMessageAcknowledged(message: SLMessage)
    fun onMessageTimeout(message: SLMessage)

    abstract class SLMessageBaseEventListener : SLMessageEventListener {
        override fun onMessageAcknowledged(message: SLMessage) {}
        override fun onMessageTimeout(message: SLMessage) {}
    }
}