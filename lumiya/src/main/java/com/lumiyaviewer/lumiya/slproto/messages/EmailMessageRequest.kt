package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageRequest : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var DataBlock_Field: DataBlock = DataBlock()

    class AgentData {
        var AgentID: UUID = UUID(0, 0)
        var SessionID: UUID = UUID(0, 0)
    }

    class DataBlock {
        var LocalID: Int = 0
        var Message: ByteArray = ByteArray(0)
        var Subject: ByteArray = ByteArray(0)
        var TimeStamp: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 32 + 4 + 4 + 1 + this.DataBlock_Field.Message.size + 1 + this.DataBlock_Field.Subject.size
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleEmailMessageRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(103.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.SessionID)
        packInt(buffer, this.DataBlock_Field.LocalID)
        packInt(buffer, this.DataBlock_Field.TimeStamp)
        packVariable(buffer, this.DataBlock_Field.Subject, 1)
        packVariable(buffer, this.DataBlock_Field.Message, 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.SessionID = unpackUUID(buffer)
        this.DataBlock_Field.LocalID = unpackInt(buffer)
        this.DataBlock_Field.TimeStamp = unpackInt(buffer)
        this.DataBlock_Field.Subject = unpackVariable(buffer, 1)
        this.DataBlock_Field.Message = unpackVariable(buffer, 1)
    }
}
