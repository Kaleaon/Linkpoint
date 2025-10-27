package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestTaskInventory : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public InventoryData InventoryData_Field = InventoryData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryData {
        public Int LocalID
    }

    public RequestTaskInventory() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleRequestTaskInventory(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 33)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.InventoryData_Field.LocalID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.InventoryData_Field.LocalID = unpackInt(byteBuffer)
    }
}
