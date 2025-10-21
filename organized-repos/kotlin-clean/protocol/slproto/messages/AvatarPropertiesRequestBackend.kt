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

    public Int CalcPayloadSize() {
        return 38
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesRequestBackend(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -86)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        packByte(byteBuffer, (Byte) this.AgentData_Field.GodLevel)
        packBoolean(byteBuffer, this.AgentData_Field.WebProfilesDisabled)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        this.AgentData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentData_Field.WebProfilesDisabled = unpackBoolean(byteBuffer)
    }
}
