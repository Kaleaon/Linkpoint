package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class ClassifiedInfoReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
    }

    class Data {
        Int Category
        Int ClassifiedFlags
        UUID ClassifiedID
        Int CreationDate
        UUID CreatorID
        byte[] Desc
        Int ExpirationDate
        byte[] Name
        UUID ParcelID
        byte[] ParcelName
        Int ParentEstate
        LLVector3d PosGlobal
        Int PriceForListing
        byte[] SimName
        UUID SnapshotID
    }

    ClassifiedInfoReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.Data_Field.Name.length + 45 + 2 + this.Data_Field.Desc.length + 16 + 4 + 16 + 1 + this.Data_Field.SimName.length + 24 + 1 + this.Data_Field.ParcelName.length + 1 + 4 + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleClassifiedInfoReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 44)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.Data_Field.ClassifiedID)
        packUUID(byteBuffer, this.Data_Field.CreatorID)
        packInt(byteBuffer, this.Data_Field.CreationDate)
        packInt(byteBuffer, this.Data_Field.ExpirationDate)
        packInt(byteBuffer, this.Data_Field.Category)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
        packVariable(byteBuffer, this.Data_Field.Desc, 2)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        packInt(byteBuffer, this.Data_Field.ParentEstate)
        packUUID(byteBuffer, this.Data_Field.SnapshotID)
        packVariable(byteBuffer, this.Data_Field.SimName, 1)
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal)
        packVariable(byteBuffer, this.Data_Field.ParcelName, 1)
        packByte(byteBuffer, (byte) this.Data_Field.ClassifiedFlags)
        packInt(byteBuffer, this.Data_Field.PriceForListing)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.ClassifiedID = unpackUUID(byteBuffer)
        this.Data_Field.CreatorID = unpackUUID(byteBuffer)
        this.Data_Field.CreationDate = unpackInt(byteBuffer)
        this.Data_Field.ExpirationDate = unpackInt(byteBuffer)
        this.Data_Field.Category = unpackInt(byteBuffer)
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.ParentEstate = unpackInt(byteBuffer)
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer)
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1)
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer)
        this.Data_Field.ParcelName = unpackVariable(byteBuffer, 1)
        this.Data_Field.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Data_Field.PriceForListing = unpackInt(byteBuffer)
    }
}
