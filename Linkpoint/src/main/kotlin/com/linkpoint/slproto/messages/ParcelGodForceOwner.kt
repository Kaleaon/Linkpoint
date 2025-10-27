package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelGodForceOwner : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Int LocalID
        public UUID OwnerID
    }

    public ParcelGodForceOwner() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return 56
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelGodForceOwner(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -42)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.OwnerID)
        packInt(byteBuffer, this.Data_Field.LocalID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerID = unpackUUID(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
    }
}
