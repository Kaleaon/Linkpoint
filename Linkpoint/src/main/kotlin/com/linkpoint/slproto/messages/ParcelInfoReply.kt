package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelInfoReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class Data {
        public Int ActualArea
        public Int AuctionID
        public Int BillableArea
        public Byte[] Desc
        public Float Dwell
        public Int Flags
        public Float GlobalX
        public Float GlobalY
        public Float GlobalZ
        public Byte[] Name
        public UUID OwnerID
        public UUID ParcelID
        public Int SalePrice
        public Byte[] SimName
        public UUID SnapshotID
    }

    public ParcelInfoReply() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.Name.length + 33 + 1 + this.Data_Field.Desc.length + 4 + 4 + 1 + 4 + 4 + 4 + 1 + this.Data_Field.SimName.length + 16 + 4 + 4 + 4 + 20
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelInfoReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 55)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        packUUID(byteBuffer, this.Data_Field.OwnerID)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
        packVariable(byteBuffer, this.Data_Field.Desc, 1)
        packInt(byteBuffer, this.Data_Field.ActualArea)
        packInt(byteBuffer, this.Data_Field.BillableArea)
        packByte(byteBuffer, (Byte) this.Data_Field.Flags)
        packFloat(byteBuffer, this.Data_Field.GlobalX)
        packFloat(byteBuffer, this.Data_Field.GlobalY)
        packFloat(byteBuffer, this.Data_Field.GlobalZ)
        packVariable(byteBuffer, this.Data_Field.SimName, 1)
        packUUID(byteBuffer, this.Data_Field.SnapshotID)
        packFloat(byteBuffer, this.Data_Field.Dwell)
        packInt(byteBuffer, this.Data_Field.SalePrice)
        packInt(byteBuffer, this.Data_Field.AuctionID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerID = unpackUUID(byteBuffer)
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
        this.Data_Field.Desc = unpackVariable(byteBuffer, 1)
        this.Data_Field.ActualArea = unpackInt(byteBuffer)
        this.Data_Field.BillableArea = unpackInt(byteBuffer)
        this.Data_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Data_Field.GlobalX = unpackFloat(byteBuffer)
        this.Data_Field.GlobalY = unpackFloat(byteBuffer)
        this.Data_Field.GlobalZ = unpackFloat(byteBuffer)
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1)
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer)
        this.Data_Field.Dwell = unpackFloat(byteBuffer)
        this.Data_Field.SalePrice = unpackInt(byteBuffer)
        this.Data_Field.AuctionID = unpackInt(byteBuffer)
    }
}
