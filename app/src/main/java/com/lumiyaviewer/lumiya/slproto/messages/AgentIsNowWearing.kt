package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentIsNowWearing : SLMessage {
    AgentData AgentData_Field
    ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class WearableData {
        UUID ItemID
        Int WearableType
    }

    AgentIsNowWearing() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        return (this.WearableData_Fields.size() * 17) + 37
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentIsNowWearing(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put(Ascii.DEL)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((byte) this.WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.ItemID)
            packByte(byteBuffer, (byte) wearableData.WearableType)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            WearableData wearableData = WearableData()
            wearableData.ItemID = unpackUUID(byteBuffer)
            wearableData.WearableType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.WearableData_Fields.add(wearableData)
        }
    }
}
