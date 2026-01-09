package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapLayerRequest : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        Int EstateID
        Int Flags
        Boolean Godlike
        UUID SessionID
    }

    MapLayerRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 45
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMapLayerRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -107)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.AgentData_Field.EstateID)
        packBoolean(byteBuffer, this.AgentData_Field.Godlike)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.AgentData_Field.EstateID = unpackInt(byteBuffer)
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer)
    }
}
