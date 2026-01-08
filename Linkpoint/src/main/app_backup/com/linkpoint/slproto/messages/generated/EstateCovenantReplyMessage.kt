package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EstateCovenantReplyMessage : SLMessage() {
    var covenantId: UUID = UUID(0L, 0L)
    var covenantTimestamp: Int = 0
    var estateName: ByteArray = ByteArray(0)
    var estateOwnerId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, covenantId)
        packInt(buffer, covenantTimestamp)
        packVariable(buffer, estateName, 1)
        packUUID(buffer, estateOwnerId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        covenantId = unpackUUID(buffer)
        covenantTimestamp = unpackInt(buffer)
        estateName = unpackVariable(buffer, 1)
        estateOwnerId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00CC.toInt()

    override fun getMessageName(): String = "EstateCovenantReply"
}
