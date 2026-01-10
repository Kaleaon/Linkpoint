package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class StartLure : SLMessage {
    AgentData AgentData_Field
    Info Info_Field
    ArrayList<TargetData> TargetData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Info {
        Int LureType
        ByteArray Message
    }

    class TargetData {
        UUID TargetID
    }

    StartLure() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.Info_Field = Info()
    }

    fun CalcPayloadSize(): Int {
        return this.Info_Field.Message.size + 2 + 36 + 1 + (this.TargetData_Fields.size() * 16)
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleStartLure(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 70)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (this as Byte).Info_Field.LureType)
        packVariable(byteBuffer, this.Info_Field.Message, 1)
        byteBuffer.put((this as Byte).TargetData_Fields.size())
        for (TargetData targetData : this.TargetData_Fields) {
            packUUID(byteBuffer, targetData.TargetID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.LureType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Info_Field.Message = unpackVariable(byteBuffer, 1)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            TargetData targetData = TargetData()
            targetData.TargetID = unpackUUID(byteBuffer)
            this.TargetData_Fields.add(targetData)
        }
    }
}
