package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class CreateNewOutfitAttachments : SLMessage {
    AgentData AgentData_Field
    HeaderData HeaderData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class HeaderData {
        UUID NewFolderID
    }

    class ObjectData {
        UUID OldFolderID
        UUID OldItemID
    }

    CreateNewOutfitAttachments() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.HeaderData_Field = HeaderData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 32) + 53
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleCreateNewOutfitAttachments(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -114)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.HeaderData_Field.NewFolderID)
        byteBuffer.put((this as byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.OldItemID)
            packUUID(byteBuffer, objectData.OldFolderID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.NewFolderID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.OldItemID = unpackUUID(byteBuffer)
            objectData.OldFolderID = unpackUUID(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
