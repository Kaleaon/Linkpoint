package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelBuyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var isGroupOwned: Boolean = false
    var removeContribution: Boolean = false
    var localId: Int = 0
    var final: Boolean = false
    var price: Int = 0
    var area: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packBoolean(buffer, isGroupOwned)
        packBoolean(buffer, removeContribution)
        packInt(buffer, localId)
        packBoolean(buffer, final)
        packInt(buffer, price)
        packInt(buffer, area)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        isGroupOwned = unpackBoolean(buffer)
        removeContribution = unpackBoolean(buffer)
        localId = unpackInt(buffer)
        final = unpackBoolean(buffer)
        price = unpackInt(buffer)
        area = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00D5

    override fun getMessageName(): String = "ParcelBuy"
}
