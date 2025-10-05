package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ChangeUserRights : SLMessage() {
    val AgentData_Field = AgentData()
    val Rights_Fields = ArrayList<Rights>()

    class AgentData {
        var AgentID: UUID? = null
    }

    class Rights {
        var AgentRelated: UUID? = null
        var RelatedRights: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (Rights_Fields.size * 20) + 21
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleChangeUserRights(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(65.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        buffer.put(Rights_Fields.size.toByte())
        for (rights in Rights_Fields) {
            packUUID(buffer, rights.AgentRelated)
            packInt(buffer, rights.RelatedRights)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val rights = Rights()
            rights.AgentRelated = unpackUUID(buffer)
            rights.RelatedRights = unpackInt(buffer)
            Rights_Fields.add(rights)
        }
    }
}