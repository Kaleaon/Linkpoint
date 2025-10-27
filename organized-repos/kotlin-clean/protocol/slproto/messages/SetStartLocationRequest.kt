package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SetStartLocationRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public StartLocationData StartLocationData_Field = StartLocationData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class StartLocationData {
        public Int LocationID
        public LLVector3 LocationLookAt
        public LLVector3 LocationPos
        public ByteArray SimName
    }

    public SetStartLocationRequest() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.StartLocationData_Field.SimName.length + 1 + 4 + 12 + 12 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetStartLocationRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 68)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.StartLocationData_Field.SimName, 1)
        packInt(byteBuffer, this.StartLocationData_Field.LocationID)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.StartLocationData_Field.SimName = unpackVariable(byteBuffer, 1)
        this.StartLocationData_Field.LocationID = unpackInt(byteBuffer)
        this.StartLocationData_Field.LocationPos = unpackLLVector3(byteBuffer)
        this.StartLocationData_Field.LocationLookAt = unpackLLVector3(byteBuffer)
    }
}
