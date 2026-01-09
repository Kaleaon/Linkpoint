package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SimWideDeletes : SLMessage {
    AgentData AgentData_Field = AgentData()
    DataBlock DataBlock_Field = DataBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class DataBlock {
        Int Flags
        UUID TargetID
    }

    SimWideDeletes() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 56
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSimWideDeletes(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -127)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.DataBlock_Field.TargetID)
        packInt(byteBuffer, this.DataBlock_Field.Flags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.DataBlock_Field.TargetID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Flags = unpackInt(byteBuffer)
    }
}
