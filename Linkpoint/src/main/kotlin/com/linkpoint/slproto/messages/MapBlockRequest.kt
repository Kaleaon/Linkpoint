package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapBlockRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public PositionData PositionData_Field = PositionData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int EstateID
        public Int Flags
        public Boolean Godlike
        public UUID SessionID
    }

    @JvmStatic
    class PositionData {
        public Int MaxX
        public Int MaxY
        public Int MinX
        public Int MinY
    }

    public MapBlockRequest() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 53
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleMapBlockRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -105)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.AgentData_Field.EstateID)
        packBoolean(byteBuffer, this.AgentData_Field.Godlike)
        packShort(byteBuffer, (Short) this.PositionData_Field.MinX)
        packShort(byteBuffer, (Short) this.PositionData_Field.MaxX)
        packShort(byteBuffer, (Short) this.PositionData_Field.MinY)
        packShort(byteBuffer, (Short) this.PositionData_Field.MaxY)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
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
