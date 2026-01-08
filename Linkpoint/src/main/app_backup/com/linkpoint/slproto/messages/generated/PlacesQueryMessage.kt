package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PlacesQueryMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var queryId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var queryText: ByteArray = ByteArray(0)
    var queryFlags: Int = 0
    var category: Int = 0
    var simName: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, queryId)
        packUUID(buffer, transactionId)
        packVariable(buffer, queryText, 1)
        packInt(buffer, queryFlags)
        packByte(buffer, category)
        packVariable(buffer, simName, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        queryText = unpackVariable(buffer, 1)
        queryFlags = unpackInt(buffer)
        category = unpackByte(buffer)
        simName = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF001D.toInt()

    override fun getMessageName(): String = "PlacesQuery"
}
