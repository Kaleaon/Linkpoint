package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelAccessListRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Int Flags
        public Int LocalID
        public Int SequenceID
    }

    public ParcelAccessListRequest() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return 48
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelAccessListRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -41)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.Data_Field.SequenceID)
        packInt(byteBuffer, this.Data_Field.Flags)
        packInt(byteBuffer, this.Data_Field.LocalID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.SequenceID = unpackInt(byteBuffer)
        this.Data_Field.Flags = unpackInt(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
    }
}
