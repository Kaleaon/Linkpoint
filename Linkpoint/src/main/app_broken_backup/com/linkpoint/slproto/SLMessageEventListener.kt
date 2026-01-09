package com.linkpoint.slproto

interface SLMessageEventListener {
    abstract class SLMessageBaseEventListener : SLMessageEventListener {
        override fun onMessageAcknowledged(sLMessage: SLMessage) {
        }

        override fun onMessageTimeout(sLMessage: SLMessage) {
        }
    }

    fun onMessageAcknowledged(sLMessage: SLMessage)

    fun onMessageTimeout(sLMessage: SLMessage)
}