package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapBlockRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    PositionData PositionData_Field = PositionData()

    class AgentData {
        UUID AgentID
        Int EstateID
        Int Flags
        Boolean Godlike
        UUID SessionID
    }

    class PositionData {
        Int MaxX
        Int MaxY
        Int MinX
        Int MinY
    }

    MapBlockRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 53
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMapBlockRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -105)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.AgentData_Field.EstateID)
        packBoolean(byteBuffer, this.AgentData_Field.Godlike)
        packShort(byteBuffer, (this as Short).PositionData_Field.MinX)
        packShort(byteBuffer, (this as Short).PositionData_Field.MaxX)
        packShort(byteBuffer, (this as Short).PositionData_Field.MinY)
        packShort(byteBuffer, (this as Short).PositionData_Field.MaxY)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.AgentData_Field.EstateID = unpackInt(byteBuffer)
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer)
        this.PositionData_Field.MinX = unpackShort(byteBuffer) & 65535
        this.PositionData_Field.MaxX = unpackShort(byteBuffer) & 65535
        this.PositionData_Field.MinY = unpackShort(byteBuffer) & 65535
        this.PositionData_Field.MaxY = unpackShort(byteBuffer) & 65535
    }
}
