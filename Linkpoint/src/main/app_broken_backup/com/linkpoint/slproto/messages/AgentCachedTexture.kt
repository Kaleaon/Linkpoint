package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentCachedTexture : SLMessage {
    AgentData AgentData_Field
    ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Int SerialNum
        UUID SessionID
    }

    class WearableData {
        UUID ID
        Int TextureIndex
    }

    constructor() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.WearableData_Fields.size() * 17) + 41
    }

    fun Handle(sLMessageHandler: SLMessageHandler)  {
        sLMessageHandler.HandleAgentCachedTexture(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Byte.MIN_VALUE)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.SerialNum)
        byteBuffer.put((this as Byte).WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.ID)
            packByte(byteBuffer, (wearableData as Byte).TextureIndex)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.SerialNum = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            WearableData wearableData = WearableData()
            wearableData.ID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.WearableData_Fields.add(wearableData)
        }
    }
}
