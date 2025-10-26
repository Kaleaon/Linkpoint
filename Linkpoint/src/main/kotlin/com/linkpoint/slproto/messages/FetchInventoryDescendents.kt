package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FetchInventoryDescendents : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public InventoryData InventoryData_Field = InventoryData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryData {
        public Boolean FetchFolders
        public Boolean FetchItems
        public UUID FolderID
        public UUID OwnerID
        public Int SortOrder
    }

    public FetchInventoryDescendents() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return 74
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleFetchInventoryDescendents(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.NAK)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.InventoryData_Field.FolderID)
        packUUID(byteBuffer, this.InventoryData_Field.OwnerID)
        packInt(byteBuffer, this.InventoryData_Field.SortOrder)
        packBoolean(byteBuffer, this.InventoryData_Field.FetchFolders)
        packBoolean(byteBuffer, this.InventoryData_Field.FetchItems)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.InventoryData_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryData_Field.OwnerID = unpackUUID(byteBuffer)
        this.InventoryData_Field.SortOrder = unpackInt(byteBuffer)
        this.InventoryData_Field.FetchFolders = unpackBoolean(byteBuffer)
        this.InventoryData_Field.FetchItems = unpackBoolean(byteBuffer)
    }
}
