package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DataHomeLocationRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var kickedFromEstateId: Int = 0
    var agentEffectiveMaturity: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, kickedFromEstateId)
        packInt(buffer, agentEffectiveMaturity)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        kickedFromEstateId = unpackInt(buffer)
        agentEffectiveMaturity = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0043

    override fun getMessageName(): String = "DataHomeLocationRequest"
}
