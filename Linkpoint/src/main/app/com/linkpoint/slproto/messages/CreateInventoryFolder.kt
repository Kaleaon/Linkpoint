package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateInventoryFolder : SLMessage {
    AgentData AgentData_Field = AgentData()
    FolderData FolderData_Field = FolderData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class FolderData {
        UUID FolderID
        ByteArray Name
        UUID ParentID
        Int Type
    }

    CreateInventoryFolder() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.FolderData_Field.Name.size + 34 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleCreateInventoryFolder(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 17)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.FolderData_Field.FolderID)
        packUUID(byteBuffer, this.FolderData_Field.ParentID)
        packByte(byteBuffer, (this as byte).FolderData_Field.Type)
        packVariable(byteBuffer, this.FolderData_Field.Name, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.FolderData_Field.FolderID = unpackUUID(byteBuffer)
        this.FolderData_Field.ParentID = unpackUUID(byteBuffer)
        this.FolderData_Field.Type = unpackByte(byteBuffer)
        this.FolderData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
