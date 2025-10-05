package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelBuy : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()
    public ParcelData ParcelData_Field = ParcelData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Boolean Final
        public UUID GroupID
        public Boolean IsGroupOwned
        public Int LocalID
        public Boolean RemoveContribution
    }

    @JvmStatic
    class ParcelData {
        public Int Area
        public Int Price
    }

    public ParcelBuy() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 67
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelBuy(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -43)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.GroupID)
        packBoolean(byteBuffer, this.Data_Field.IsGroupOwned)
        packBoolean(byteBuffer, this.Data_Field.RemoveContribution)
        packInt(byteBuffer, this.Data_Field.LocalID)
        packBoolean(byteBuffer, this.Data_Field.Final)
        packInt(byteBuffer, this.ParcelData_Field.Price)
        packInt(byteBuffer, this.ParcelData_Field.Area)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.GroupID = unpackUUID(byteBuffer)
        this.Data_Field.IsGroupOwned = unpackBoolean(byteBuffer)
        this.Data_Field.RemoveContribution = unpackBoolean(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
        this.Data_Field.Final = unpackBoolean(byteBuffer)
        this.ParcelData_Field.Price = unpackInt(byteBuffer)
        this.ParcelData_Field.Area = unpackInt(byteBuffer)
    }
}
