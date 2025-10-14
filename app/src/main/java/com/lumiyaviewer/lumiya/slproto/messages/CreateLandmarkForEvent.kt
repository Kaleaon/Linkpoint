package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateLandmarkForEvent : SLMessage {
    AgentData AgentData_Field = AgentData()
    EventData EventData_Field = EventData()
    InventoryBlock InventoryBlock_Field = InventoryBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class EventData {
        Int EventID
    }

    class InventoryBlock {
        UUID FolderID
        byte[] Name
    }

    CreateLandmarkForEvent() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 17 + 40
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateLandmarkForEvent(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 50)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.EventData_Field.EventID)
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID)
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer)
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
