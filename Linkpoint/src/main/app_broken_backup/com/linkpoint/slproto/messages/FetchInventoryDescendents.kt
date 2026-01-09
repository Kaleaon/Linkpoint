package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FetchInventoryDescendents : SLMessage {
    AgentData AgentData_Field = AgentData()
    InventoryData InventoryData_Field = InventoryData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class InventoryData {
        Boolean FetchFolders
        Boolean FetchItems
        UUID FolderID
        UUID OwnerID
        Int SortOrder
    }

    FetchInventoryDescendents() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 74
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleFetchInventoryDescendents(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put(Ascii.NAK)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.InventoryData_Field.FolderID)
        packUUID(byteBuffer, this.InventoryData_Field.OwnerID)
        packInt(byteBuffer, this.InventoryData_Field.SortOrder)
        packBoolean(byteBuffer, this.InventoryData_Field.FetchFolders)
        packBoolean(byteBuffer, this.InventoryData_Field.FetchItems)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.InventoryData_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryData_Field.OwnerID = unpackUUID(byteBuffer)
        this.InventoryData_Field.SortOrder = unpackInt(byteBuffer)
        this.InventoryData_Field.FetchFolders = unpackBoolean(byteBuffer)
        this.InventoryData_Field.FetchItems = unpackBoolean(byteBuffer)
    }
}
