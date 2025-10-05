package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDwellReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class Data {
        public Float Dwell
        public Int LocalID
        public UUID ParcelID
    }

    public ParcelDwellReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 44
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelDwellReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -37)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.Data_Field.LocalID)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        packFloat(byteBuffer, this.Data_Field.Dwell)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.Dwell = unpackFloat(byteBuffer)
    }
}
