package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class GrantUserRights : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Rights> Rights_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Rights {
        public UUID AgentRelated
        public Int RelatedRights
    }

    public GrantUserRights() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.Rights_Fields.size() * 20) + 37
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGrantUserRights(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 64)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.Rights_Fields.size())
        for (Rights rights : this.Rights_Fields) {
            packUUID(byteBuffer, rights.AgentRelated)
            packInt(byteBuffer, rights.RelatedRights)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val rights: Rights = Rights()
            rights.AgentRelated = unpackUUID(byteBuffer)
            rights.RelatedRights = unpackInt(byteBuffer)
            this.Rights_Fields.add(rights)
        }
    }
}
