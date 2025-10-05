package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StateSave : SLMessage() {
    val AgentData_Field = AgentData()
    val DataBlock_Field = DataBlock()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class DataBlock {
        lateinit var Filename: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return DataBlock_Field.Filename.size + 1 + 36
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleStateSave(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(Ascii.DEL)
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packVariable(buffer, DataBlock_Field.Filename, 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        DataBlock_Field.Filename = unpackVariable(buffer, 1)
    }
}