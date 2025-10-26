package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateInventoryFolder : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public FolderData FolderData_Field = FolderData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class FolderData {
        public UUID FolderID
        public ByteArray Name
        public UUID ParentID
        public Int Type
    }

    public CreateInventoryFolder() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.FolderData_Field.Name.length + 34 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateInventoryFolder(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 17)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.FolderData_Field.FolderID)
        packUUID(byteBuffer, this.FolderData_Field.ParentID)
        packByte(byteBuffer, (Byte) this.FolderData_Field.Type)
        packVariable(byteBuffer, this.FolderData_Field.Name, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.FolderData_Field.FolderID = unpackUUID(byteBuffer)
        this.FolderData_Field.ParentID = unpackUUID(byteBuffer)
        this.FolderData_Field.Type = unpackByte(byteBuffer)
        this.FolderData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
