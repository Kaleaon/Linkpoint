package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentHeightWidth : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public HeightWidthBlock HeightWidthBlock_Field = HeightWidthBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int CircuitCode
        public UUID SessionID
    }

    @JvmStatic
    class HeightWidthBlock {
        public Int GenCounter
        public Int Height
        public Int Width
    }

    public AgentHeightWidth() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 48
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentHeightWidth(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 83)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.CircuitCode)
        packInt(byteBuffer, this.HeightWidthBlock_Field.GenCounter)
        packShort(byteBuffer, (Short) this.HeightWidthBlock_Field.Height)
        packShort(byteBuffer, (Short) this.HeightWidthBlock_Field.Width)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer)
        this.HeightWidthBlock_Field.GenCounter = unpackInt(byteBuffer)
        this.HeightWidthBlock_Field.Height = unpackShort(byteBuffer) & 65535
        this.HeightWidthBlock_Field.Width = unpackShort(byteBuffer) & 65535
    }
}
