package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPickerRequestBackend : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        Int GodLevel
        UUID QueryID
        UUID SessionID
    }

    class Data {
        ByteArray Name
    }

    AvatarPickerRequestBackend() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.Name.size + 1 + 53
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleAvatarPickerRequestBackend(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put(Ascii.ESC)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        packByte(byteBuffer, (this as byte).AgentData_Field.GodLevel)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer)
        this.AgentData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
