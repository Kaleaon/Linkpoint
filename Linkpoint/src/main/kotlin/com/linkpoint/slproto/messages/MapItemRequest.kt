package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapItemRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public RequestData RequestData_Field = RequestData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int EstateID
        public Int Flags
        public Boolean Godlike
        public UUID SessionID
    }

    @JvmStatic
    class RequestData {
        public Int ItemType
        public Long RegionHandle
    }

    public MapItemRequest() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 57
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleMapItemRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.AgentData_Field.EstateID = unpackInt(byteBuffer)
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer)
        this.RequestData_Field.ItemType = unpackInt(byteBuffer)
        this.RequestData_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
