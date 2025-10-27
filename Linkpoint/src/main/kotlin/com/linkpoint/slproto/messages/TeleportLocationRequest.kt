package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLocationRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Info Info_Field = Info()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Info {
        public LLVector3 LookAt
        public LLVector3 Position
        public Long RegionHandle
    }

    public TeleportLocationRequest() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 68
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleTeleportLocationRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 63)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLong(byteBuffer, this.Info_Field.RegionHandle)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.RegionHandle = unpackLong(byteBuffer)
        this.Info_Field.Position = unpackLLVector3(byteBuffer)
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
