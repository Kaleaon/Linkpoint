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

    public Int CalcPayloadSize() {
        return (this.Rights_Fields.size() * 20) + 37
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGrantUserRights(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Rights rights = Rights()
            rights.AgentRelated = unpackUUID(byteBuffer)
            rights.RelatedRights = unpackInt(byteBuffer)
            this.Rights_Fields.add(rights)
        }
    }
}
