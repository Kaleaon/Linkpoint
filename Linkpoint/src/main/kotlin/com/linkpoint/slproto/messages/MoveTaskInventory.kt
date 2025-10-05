package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoveTaskInventory : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public InventoryData InventoryData_Field = InventoryData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID FolderID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryData {
        public UUID ItemID
        public Int LocalID
    }

    public MoveTaskInventory() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 72
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoveTaskInventory(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 32)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.FolderID)
        packInt(byteBuffer, this.InventoryData_Field.LocalID)
        packUUID(byteBuffer, this.InventoryData_Field.ItemID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryData_Field.LocalID = unpackInt(byteBuffer)
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
