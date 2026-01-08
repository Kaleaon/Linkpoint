package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class EmailMessageReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var DataBlock_Field: DataBlock = DataBlock()

    class AgentData {
        var AgentID: UUID = UUID(0, 0)
        var GroupID: UUID = UUID(0, 0)
    }

    class DataBlock {
        var EmailID: UUID = UUID(0, 0)
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 48
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleEmailMessageReply(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(104.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.GroupID)
        packUUID(buffer, this.DataBlock_Field.EmailID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.GroupID = unpackUUID(buffer)
        this.DataBlock_Field.EmailID = unpackUUID(buffer)
    }
}
