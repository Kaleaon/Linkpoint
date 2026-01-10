package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionHandshakeReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    RegionInfo RegionInfo_Field = RegionInfo()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class RegionInfo {
        Int Flags
    }

    RegionHandshakeReply() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRegionHandshakeReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -107)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.RegionInfo_Field.Flags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RegionInfo_Field.Flags = unpackInt(byteBuffer)
    }
}
