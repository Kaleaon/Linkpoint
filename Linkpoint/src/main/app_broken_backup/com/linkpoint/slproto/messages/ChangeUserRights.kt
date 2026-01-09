package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ChangeUserRights : SLMessage {
    AgentData AgentData_Field
    ArrayList<Rights> Rights_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class Rights {
        UUID AgentRelated
        Int RelatedRights
    }

    ChangeUserRights() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.Rights_Fields.size() * 20) + 21
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleChangeUserRights(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 65)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((this as byte).Rights_Fields.size())
        for (Rights rights : this.Rights_Fields) {
            packUUID(byteBuffer, rights.AgentRelated)
            packInt(byteBuffer, rights.RelatedRights)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Rights rights = Rights()
            rights.AgentRelated = unpackUUID(byteBuffer)
            rights.RelatedRights = unpackInt(byteBuffer)
            this.Rights_Fields.add(rights)
        }
    }
}
