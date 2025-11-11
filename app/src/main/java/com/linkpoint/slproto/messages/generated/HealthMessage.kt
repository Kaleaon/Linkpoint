package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class HealthMessage : SLMessage() {
    var health: Float = 0f


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packFloat(buffer, health)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        health = unpackFloat(buffer)
    }

    override fun getMessageID(): Int = 0x0000008A

    override fun getMessageName(): String = "HealthMessage"
}
