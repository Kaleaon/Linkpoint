package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RemoveInventoryObjects : SLMessage {
    AgentData AgentData_Field
    ArrayList<FolderData> FolderData_Fields = ArrayList<>()
    ArrayList<ItemData> ItemData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class FolderData {
        UUID FolderID
    }

    class ItemData {
        UUID ItemID
    }

    RemoveInventoryObjects() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.FolderData_Fields.size() * 16) + 37 + 1 + (this.ItemData_Fields.size() * 16)
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRemoveInventoryObjects(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.FS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as Byte).FolderData_Fields.size())
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID)
        }
        byteBuffer.put((this as Byte).ItemData_Fields.size())
        for (ItemData itemData : this.ItemData_Fields) {
            packUUID(byteBuffer, itemData.ItemID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            FolderData folderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            this.FolderData_Fields.add(folderData)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            ItemData itemData = ItemData()
            itemData.ItemID = unpackUUID(byteBuffer)
            this.ItemData_Fields.add(itemData)
        }
    }
}
