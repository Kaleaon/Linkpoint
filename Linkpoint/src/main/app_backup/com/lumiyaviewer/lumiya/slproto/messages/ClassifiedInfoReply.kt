package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class ClassifiedInfoReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Field: Data = Data()

    class AgentData {
        var AgentID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
    }

    class Data {
        var ClassifiedFlags: Int = 0
        var ClassifiedID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var CreationDate: Long = 0
        var CreatorID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var Desc: ByteArray = ByteArray(0)
        var ExpirationDate: Long = 0
        var Name: ByteArray = ByteArray(0)
        var ParcelID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var ParentEstate: Int = 0
        var PosGlobal: com.lumiyaviewer.lumiya.slproto.types.LLVector3 = com.lumiyaviewer.lumiya.slproto.types.LLVector3()
        var PriceForListing: Int = 0
        var ScopeID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var SnapshotID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
    }

    override fun CalcPayloadSize(): Int {
        return this.Data_Field.Name.size + 101 + 1 + 2 + this.Data_Field.Desc.size + 2
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleClassifiedInfoReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-2).toByte())
        byteBuffer.put(this.AgentData_Field.AgentID.data)
        byteBuffer.put(this.Data_Field.ClassifiedID.data)
        byteBuffer.put(this.Data_Field.CreatorID.data)
        byteBuffer.putLong(this.Data_Field.CreationDate)
        byteBuffer.putLong(this.Data_Field.ExpirationDate)
        byteBuffer.putInt(this.Data_Field.ParentEstate)
        byteBuffer.put(this.Data_Field.SnapshotID.data)
        byteBuffer.put(this.Data_Field.ScopeID.data)
        byteBuffer.putInt(this.Data_Field.ClassifiedFlags)
        byteBuffer.putInt(this.Data_Field.PriceForListing)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
        packVariable(byteBuffer, this.Data_Field.Desc, 2)
        byteBuffer.put(this.Data_Field.ParcelID.data)
        packLLVector3(byteBuffer, this.Data_Field.PosGlobal)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.Data_Field.ClassifiedID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.Data_Field.CreatorID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.Data_Field.CreationDate = byteBuffer.long
        this.Data_Field.ExpirationDate = byteBuffer.long
        this.Data_Field.ParentEstate = byteBuffer.int
        this.Data_Field.SnapshotID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.Data_Field.ScopeID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.Data_Field.ClassifiedFlags = byteBuffer.int
        this.Data_Field.PriceForListing = byteBuffer.int
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2)
        this.Data_Field.ParcelID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.Data_Field.PosGlobal = unpackLLVector3(byteBuffer)
    }
}
