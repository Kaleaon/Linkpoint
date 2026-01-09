package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentHeightWidth : SLMessage {
    AgentData AgentData_Field = AgentData()
    HeightWidthBlock HeightWidthBlock_Field = HeightWidthBlock()

    class AgentData {
        UUID AgentID
        Int CircuitCode
        UUID SessionID
    }

    class HeightWidthBlock {
        Int GenCounter
        Int Height
        Int Width
    }

    AgentHeightWidth() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 48
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleAgentHeightWidth(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 83)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.CircuitCode)
        packInt(byteBuffer, this.HeightWidthBlock_Field.GenCounter)
        packShort(byteBuffer, (this as short).HeightWidthBlock_Field.Height)
        packShort(byteBuffer, (this as short).HeightWidthBlock_Field.Width)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer)
        this.HeightWidthBlock_Field.GenCounter = unpackInt(byteBuffer)
        this.HeightWidthBlock_Field.Height = unpackShort(byteBuffer) & 65535
        this.HeightWidthBlock_Field.Width = unpackShort(byteBuffer) & 65535
    }
}
