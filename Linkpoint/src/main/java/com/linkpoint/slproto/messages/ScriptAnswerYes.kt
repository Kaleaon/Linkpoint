package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptAnswerYes : SLMessage() {
    val AgentData_Field = AgentData()
    val Data_Field = Data()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class Data {
        var TaskID: UUID? = null
        var ItemID: UUID? = null
        var Questions: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 72

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleScriptAnswerYes(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-124).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, Data_Field.TaskID)
        packUUID(buffer, Data_Field.ItemID)
        packInt(buffer, Data_Field.Questions)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        Data_Field.TaskID = unpackUUID(buffer)
        Data_Field.ItemID = unpackUUID(buffer)
        Data_Field.Questions = unpackInt(buffer)
    }
}