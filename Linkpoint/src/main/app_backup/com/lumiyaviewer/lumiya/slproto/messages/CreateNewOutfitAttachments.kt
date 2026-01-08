package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class CreateNewOutfitAttachments : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var HeaderData_Field: HeaderData = HeaderData()
    var ObjectData_Fields: ArrayList<ObjectData> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class HeaderData {
        var NewFolderID: UUID = UUIDPool.ZeroUUID
    }

    class ObjectData {
        var OldFolderID: UUID = UUIDPool.ZeroUUID
        var OldItemID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size * 32) + 53
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCreateNewOutfitAttachments(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put((-114).toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.HeaderData_Field.NewFolderID)
        byteBuffer.put(this.ObjectData_Fields.size.toByte())
        for (objectData in this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.OldItemID)
            packUUID(byteBuffer, objectData.OldFolderID)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.NewFolderID = unpackUUID(byteBuffer)
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val objectData = ObjectData()
            objectData.OldItemID = unpackUUID(byteBuffer)
            objectData.OldFolderID = unpackUUID(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
