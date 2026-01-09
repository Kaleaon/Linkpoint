package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptAnswerYes : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        UUID ItemID
        Int Questions
        UUID TaskID
    }

    ScriptAnswerYes() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 72
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptAnswerYes(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -124)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.TaskID)
        packUUID(byteBuffer, this.Data_Field.ItemID)
        packInt(byteBuffer, this.Data_Field.Questions)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.TaskID = unpackUUID(byteBuffer)
        this.Data_Field.ItemID = unpackUUID(byteBuffer)
        this.Data_Field.Questions = unpackInt(byteBuffer)
    }
}
