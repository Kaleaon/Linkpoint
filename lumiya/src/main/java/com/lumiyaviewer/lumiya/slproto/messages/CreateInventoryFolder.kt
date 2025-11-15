package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        byte[] Name
        UUID ParentID
        Int Type
    }

    CreateInventoryFolder() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.FolderData_Field.Name.length + 34 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateInventoryFolder(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 17)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.FolderData_Field.FolderID)
        packUUID(byteBuffer, this.FolderData_Field.ParentID)
        packByte(byteBuffer, (byte) this.FolderData_Field.Type)
        packVariable(byteBuffer, this.FolderData_Field.Name, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.FolderData_Field.FolderID = unpackUUID(byteBuffer)
        this.FolderData_Field.ParentID = unpackUUID(byteBuffer)
        this.FolderData_Field.Type = unpackByte(byteBuffer)
        this.FolderData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
