package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesRequestBackend : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        UUID AvatarID
        Int GodLevel
        Boolean WebProfilesDisabled
    }

    AvatarPropertiesRequestBackend() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 38
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleAvatarPropertiesRequestBackend(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -86)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        packByte(byteBuffer, (this as byte).AgentData_Field.GodLevel)
        packBoolean(byteBuffer, this.AgentData_Field.WebProfilesDisabled)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        this.AgentData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentData_Field.WebProfilesDisabled = unpackBoolean(byteBuffer)
    }
}
