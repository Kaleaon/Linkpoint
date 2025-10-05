package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PurgeInventoryDescendents : SLMessage() {
    val AgentData_Field = AgentData()
    val InventoryData_Field = InventoryData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class InventoryData {
        var FolderID: UUID? = null
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int = 52

    override fun Handle(handler: SLMessageHandler) {
        handler.HandlePurgeInventoryDescendents(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(Ascii.GS)
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, InventoryData_Field.FolderID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        InventoryData_Field.FolderID = unpackUUID(buffer)
    }
}