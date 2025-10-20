package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateLandmarkForEvent : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public EventData EventData_Field = EventData()
    public InventoryBlock InventoryBlock_Field = InventoryBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class EventData {
        public Int EventID
    }

    @JvmStatic
    class InventoryBlock {
        public UUID FolderID
        public Byte[] Name
    }

    public CreateLandmarkForEvent() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 17 + 40
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateLandmarkForEvent(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 50)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.EventData_Field.EventID)
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID)
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
