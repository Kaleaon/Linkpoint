package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        Byte[] Name
        UUID ParentID
        Int Type
    }

    UpdateInventoryFolder() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.FolderData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((FolderData) it.next()).Name.length + 34 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateInventoryFolder(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.DC2)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.FolderData_Fields.size())
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID)
            packUUID(byteBuffer, folderData.ParentID)
            packByte(byteBuffer, (Byte) folderData.Type)
            packVariable(byteBuffer, folderData.Name, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            FolderData folderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            folderData.ParentID = unpackUUID(byteBuffer)
            folderData.Type = unpackByte(byteBuffer)
            folderData.Name = unpackVariable(byteBuffer, 1)
            this.FolderData_Fields.add(folderData)
        }
    }
}
