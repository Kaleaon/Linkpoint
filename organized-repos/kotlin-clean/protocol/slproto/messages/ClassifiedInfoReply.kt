package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class ClassifiedInfoReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class Data {
        public Int Category
        public Int ClassifiedFlags
        public UUID ClassifiedID
        public Int CreationDate
        public UUID CreatorID
        public ByteArray Desc
        public Int ExpirationDate
        public ByteArray Name
        public UUID ParcelID
        public ByteArray ParcelName
        public Int ParentEstate
        public LLVector3d PosGlobal
        public Int PriceForListing
        public ByteArray SimName
        public UUID SnapshotID
    }

    public ClassifiedInfoReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.Name.length + 45 + 2 + this.Data_Field.Desc.length + 16 + 4 + 16 + 1 + this.Data_Field.SimName.length + 24 + 1 + this.Data_Field.ParcelName.length + 1 + 4 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleClassifiedInfoReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 44)
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
        packByte(byteBuffer, (Byte) this.Data_Field.ClassifiedFlags)
        packInt(byteBuffer, this.Data_Field.PriceForListing)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
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
