package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class CreateInventoryFolder : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var FolderData_Field: FolderData = FolderData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class FolderData {
        var FolderID: UUID = UUIDPool.ZeroUUID
        var Name: ByteArray = ByteArray(0)
        var ParentID: UUID = UUIDPool.ZeroUUID
        var Type: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return this.FolderData_Field.Name.size + 17 + 1 + 32 + 16 + 16 + 1
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCreateInventoryFolder(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(35.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.FolderData_Field.FolderID)
        packUUID(byteBuffer, this.FolderData_Field.ParentID)
        packByte(byteBuffer, this.FolderData_Field.Type.toByte())
        packVariable(byteBuffer, this.FolderData_Field.Name, 1)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.FolderData_Field.FolderID = unpackUUID(byteBuffer)
        this.FolderData_Field.ParentID = unpackUUID(byteBuffer)
        this.FolderData_Field.Type = unpackByte(byteBuffer).toInt()
        this.FolderData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
