package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDwellReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    var parcelId: UUID = UUID(0L, 0L)
    var dwell: Float = 0f


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, localId)
        packUUID(buffer, parcelId)
        packFloat(buffer, dwell)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        parcelId = unpackUUID(buffer)
        dwell = unpackFloat(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00DB

    override fun getMessageName(): String = "ParcelDwellReply"
}
