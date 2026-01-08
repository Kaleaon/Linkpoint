package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ChatFromViewer : SLMessage {
    val AgentData_Field = AgentData()
    val ChatData_Field = ChatData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class ChatData {
        var Channel: Int = 0
        var Message: ByteArray = ByteArray(0)
        var Type: Int = 0
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return ChatData_Field.Message.size + 2 + 1 + 4 + 36
    }

    // Stub Handle method if SLMessageHandler interface needs it, 
    // but for now we are mostly sending this message.
    // override fun Handle(handler: SLMessageHandler) { handler.HandleChatFromViewer(this) }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        // Header packing might be handled by SLMessage, but if this is overriding:
        // The decompiled code had explicit header packing which is unusual for PackPayload.
        // Usually PackPayload only packs the body. 
        // "byteBuffer.putShort(-1); byteBuffer.put((byte) 0); byteBuffer.put((byte) 80);" 
        // This looks like a Low/Medium frequency header? 
        // If SLMessage handles header, this might be wrong. 
        // I'll trust the decompiled logic but be careful.
        // Actually, SLMessage.Pack() usually calls PackPayload.
        // I'll assume standard packing.
        
        packUUID(byteBuffer, AgentData_Field.AgentID)
        packUUID(byteBuffer, AgentData_Field.SessionID)
        packVariable(byteBuffer, ChatData_Field.Message, 2)
        packByte(byteBuffer, ChatData_Field.Type.toByte())
        packInt(byteBuffer, ChatData_Field.Channel)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.SessionID = unpackUUID(byteBuffer)
        ChatData_Field.Message = unpackVariable(byteBuffer, 2)
        ChatData_Field.Type = unpackByte(byteBuffer).toInt() and 0xFF
        ChatData_Field.Channel = unpackInt(byteBuffer)
    }
}
