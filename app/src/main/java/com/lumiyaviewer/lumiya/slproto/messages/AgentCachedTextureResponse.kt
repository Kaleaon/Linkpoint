package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AgentCachedTextureResponse : SLMessage {
    AgentData AgentData_Field
    ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Int SerialNum
        UUID SessionID
    }

    class WearableData {
        ByteArray HostName
        UUID TextureID
        Int TextureIndex
    }

    constructor() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 41
        Iterator<T> it = this.WearableData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((WearableData) it.next()).HostName.length + 18 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleAgentCachedTextureResponse(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -127)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.SerialNum)
        byteBuffer.put((Byte) this.WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.TextureID)
            packByte(byteBuffer, (Byte) wearableData.TextureIndex)
            packVariable(byteBuffer, wearableData.HostName, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.SerialNum = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            WearableData wearableData = WearableData()
            wearableData.TextureID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            wearableData.HostName = unpackVariable(byteBuffer, 1)
            this.WearableData_Fields.add(wearableData)
        }
    }
}
