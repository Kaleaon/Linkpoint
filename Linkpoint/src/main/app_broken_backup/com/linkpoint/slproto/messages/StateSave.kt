package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StateSave : SLMessage {
    AgentData AgentData_Field = AgentData()
    DataBlock DataBlock_Field = DataBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class DataBlock {
        ByteArray Filename
    }

    StateSave() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.Filename.size + 1 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleStateSave(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.DEL)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.DataBlock_Field.Filename, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Filename = unpackVariable(byteBuffer, 1)
    }
}
