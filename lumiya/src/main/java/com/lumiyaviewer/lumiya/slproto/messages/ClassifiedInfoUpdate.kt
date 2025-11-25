package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class ClassifiedInfoUpdate : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Field: Data = Data()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class Data {
        var Category: Int = 0
        var ClassifiedFlags: Int = 0
        var ClassifiedID: UUID = UUIDPool.ZeroUUID
        var Desc: ByteArray = ByteArray(0)
        var Name: ByteArray = ByteArray(0)
        var ParcelID: UUID = UUIDPool.ZeroUUID
        var ParentEstate: Int = 0
        var PosGlobal: LLVector3d = LLVector3d()
        var PriceForListing: Int = 0
        var SnapshotID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return this.Data_Field.Name.size + 21 + 2 + this.Data_Field.Desc.size + 16 + 4 + 16 + 24 + 1 + 4 + 36
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleClassifiedInfoUpdate(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(45.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.ClassifiedID)
        byteBuffer.putInt(this.Data_Field.Category)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
        packVariable(byteBuffer, this.Data_Field.Desc, 2)
        packUUID(byteBuffer, this.Data_Field.ParcelID)
        byteBuffer.putInt(this.Data_Field.ParentEstate)
        packUUID(byteBuffer, this.Data_Field.SnapshotID)
        packLLVector3d(byteBuffer, this.Data_Field.PosGlobal)
        byteBuffer.put(this.Data_Field.ClassifiedFlags.toByte())
        byteBuffer.putInt(this.Data_Field.PriceForListing)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.ClassifiedID = unpackUUID(byteBuffer)
        this.Data_Field.Category = byteBuffer.int
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
        this.Data_Field.Desc = unpackVariable(byteBuffer, 2)
        this.Data_Field.ParcelID = unpackUUID(byteBuffer)
        this.Data_Field.ParentEstate = byteBuffer.int
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer)
        this.Data_Field.PosGlobal = unpackLLVector3d(byteBuffer)
        this.Data_Field.ClassifiedFlags = byteBuffer.get().toInt() and 0xFF
        this.Data_Field.PriceForListing = byteBuffer.int
    }
}
