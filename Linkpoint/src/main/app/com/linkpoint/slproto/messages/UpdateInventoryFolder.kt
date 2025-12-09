package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class UpdateInventoryFolder : SLMessage {
    AgentData AgentData_Field
    ArrayList<FolderData> FolderData_Fields = ArrayList<>()

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

    UpdateInventoryFolder() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 37
        Iterator<T> it = this.FolderData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as FolderData).next()).Name.size + 34 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleUpdateInventoryFolder(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.DC2)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as Byte).FolderData_Fields.size())
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID)
            packUUID(byteBuffer, folderData.ParentID)
            packByte(byteBuffer, (folderData as Byte).Type)
            packVariable(byteBuffer, folderData.Name, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            FolderData folderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            folderData.ParentID = unpackUUID(byteBuffer)
            folderData.Type = unpackByte(byteBuffer)
            folderData.Name = unpackVariable(byteBuffer, 1)
            this.FolderData_Fields.add(folderData)
        }
    }
}
