package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapItemRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    RequestData RequestData_Field = RequestData()

    class AgentData {
        UUID AgentID
        Int EstateID
        Int Flags
        Boolean Godlike
        UUID SessionID
    }

    class RequestData {
        Int ItemType
        Long RegionHandle
    }

    MapItemRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 57
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleMapItemRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -102)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.AgentData_Field.EstateID)
        packBoolean(byteBuffer, this.AgentData_Field.Godlike)
        packInt(byteBuffer, this.RequestData_Field.ItemType)
        packLong(byteBuffer, this.RequestData_Field.RegionHandle)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.AgentData_Field.EstateID = unpackInt(byteBuffer)
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer)
        this.RequestData_Field.ItemType = unpackInt(byteBuffer)
        this.RequestData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
