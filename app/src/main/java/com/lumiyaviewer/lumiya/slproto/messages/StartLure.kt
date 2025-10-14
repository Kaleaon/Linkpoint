package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        Byte[] Message
    }

    class TargetData {
        UUID TargetID
    }

    StartLure() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.Info_Field = Info()
    }

    Int CalcPayloadSize() {
        return this.Info_Field.Message.length + 2 + 36 + 1 + (this.TargetData_Fields.size() * 16)
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartLure(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 70)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (Byte) this.Info_Field.LureType)
        packVariable(byteBuffer, this.Info_Field.Message, 1)
        byteBuffer.put((Byte) this.TargetData_Fields.size())
        for (TargetData targetData : this.TargetData_Fields) {
            packUUID(byteBuffer, targetData.TargetID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.LureType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Info_Field.Message = unpackVariable(byteBuffer, 1)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            TargetData targetData = TargetData()
            targetData.TargetID = unpackUUID(byteBuffer)
            this.TargetData_Fields.add(targetData)
        }
    }
}
