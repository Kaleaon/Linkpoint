package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelInfoReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
    }

    class Data {
        Int ActualArea
        Int AuctionID
        Int BillableArea
        ByteArray Desc
        Float Dwell
        Int Flags
        Float GlobalX
        Float GlobalY
        Float GlobalZ
        ByteArray Name
        UUID OwnerID
        UUID ParcelID
        Int SalePrice
        ByteArray SimName
        UUID SnapshotID
    }

    ParcelInfoReply() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.Name.size + 33 + 1 + this.Data_Field.Desc.size + 4 + 4 + 1 + 4 + 4 + 4 + 1 + this.Data_Field.SimName.size + 16 + 4 + 4 + 4 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelInfoReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
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
        packByte(byteBuffer, (this as Byte).Data_Field.Flags)
        packFloat(byteBuffer, this.Data_Field.GlobalX)
        packFloat(byteBuffer, this.Data_Field.GlobalY)
        packFloat(byteBuffer, this.Data_Field.GlobalZ)
        packVariable(byteBuffer, this.Data_Field.SimName, 1)
        packUUID(byteBuffer, this.Data_Field.SnapshotID)
        packFloat(byteBuffer, this.Data_Field.Dwell)
        packInt(byteBuffer, this.Data_Field.SalePrice)
        packInt(byteBuffer, this.Data_Field.AuctionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
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
