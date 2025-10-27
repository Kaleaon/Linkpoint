package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesRequestBackend : SLMessage() {
    public AgentData AgentData_Field = AgentData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID AvatarID
        public Int GodLevel
        public Boolean WebProfilesDisabled
    }

    public AvatarPropertiesRequestBackend() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 38
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesRequestBackend(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -86)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        packByte(byteBuffer, (Byte) this.AgentData_Field.GodLevel)
        packBoolean(byteBuffer, this.AgentData_Field.WebProfilesDisabled)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        this.AgentData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentData_Field.WebProfilesDisabled = unpackBoolean(byteBuffer)
    }
}
