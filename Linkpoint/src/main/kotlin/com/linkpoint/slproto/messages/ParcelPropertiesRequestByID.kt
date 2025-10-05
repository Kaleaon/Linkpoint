package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelPropertiesRequestByID : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ParcelData ParcelData_Field = ParcelData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ParcelData {
        public Int LocalID
        public Int SequenceID
    }

    public ParcelPropertiesRequestByID() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 44
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelPropertiesRequestByID(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -59)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.SequenceID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
    }
}
