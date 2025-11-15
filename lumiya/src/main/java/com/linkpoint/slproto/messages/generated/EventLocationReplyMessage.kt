package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class EventLocationReplyMessage : SLMessage() {
    var queryId: UUID = UUID(0L, 0L)
    var success: Boolean = false
    var regionId: UUID = UUID(0L, 0L)
    var regionPos: LLVector3 = LLVector3()


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, queryId)
        packBoolean(buffer, success)
        packUUID(buffer, regionId)
        regionPos.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        queryId = unpackUUID(buffer)
        success = unpackBoolean(buffer)
        regionId = unpackUUID(buffer)
        regionPos = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0134

    override fun getMessageName(): String = "EventLocationReply"
}
