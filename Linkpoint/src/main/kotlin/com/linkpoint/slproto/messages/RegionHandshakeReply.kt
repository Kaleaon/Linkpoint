package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionHandshakeReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public RegionInfo RegionInfo_Field = RegionInfo()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class RegionInfo {
        public Int Flags
    }

    public RegionHandshakeReply() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 40
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandshakeReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -107)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.RegionInfo_Field.Flags)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RegionInfo_Field.Flags = unpackInt(byteBuffer)
    }
}
