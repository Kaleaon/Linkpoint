package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class EventInfoReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var eventId: Int = 0
    var creator: ByteArray = ByteArray(0)
    var name: ByteArray = ByteArray(0)
    var category: ByteArray = ByteArray(0)
    var desc: ByteArray = ByteArray(0)
    var date: ByteArray = ByteArray(0)
    var dateUtc: Int = 0
    var duration: Int = 0
    var cover: Int = 0
    var amount: Int = 0
    var simName: ByteArray = ByteArray(0)
    var globalPos: LLVector3d = LLVector3d()
    var eventFlags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, eventId)
        packVariable(buffer, creator, 1)
        packVariable(buffer, name, 1)
        packVariable(buffer, category, 1)
        packVariable(buffer, desc, 2)
        packVariable(buffer, date, 1)
        packInt(buffer, dateUtc)
        packInt(buffer, duration)
        packInt(buffer, cover)
        packInt(buffer, amount)
        packVariable(buffer, simName, 1)
        globalPos.pack(buffer)
        packInt(buffer, eventFlags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        eventId = unpackInt(buffer)
        creator = unpackVariable(buffer, 1)
        name = unpackVariable(buffer, 1)
        category = unpackVariable(buffer, 1)
        desc = unpackVariable(buffer, 2)
        date = unpackVariable(buffer, 1)
        dateUtc = unpackInt(buffer)
        duration = unpackInt(buffer)
        cover = unpackInt(buffer)
        amount = unpackInt(buffer)
        simName = unpackVariable(buffer, 1)
        globalPos = LLVector3d.unpack(buffer)
        eventFlags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00B4.toInt()

    override fun getMessageName(): String = "EventInfoReply"
}
