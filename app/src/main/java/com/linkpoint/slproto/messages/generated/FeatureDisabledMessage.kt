package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FeatureDisabledMessage : SLMessage() {
    var errorMessage: ByteArray = ByteArray(0)
    var agentId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, errorMessage, 1)
        packUUID(buffer, agentId)
        packUUID(buffer, transactionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        errorMessage = unpackVariable(buffer, 1)
        agentId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0x00000013

    override fun getMessageName(): String = "FeatureDisabled"
}
