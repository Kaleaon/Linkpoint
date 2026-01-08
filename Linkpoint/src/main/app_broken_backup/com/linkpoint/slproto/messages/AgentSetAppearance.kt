package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentSetAppearance : SLMessage {
    AgentData AgentData_Field
    ObjectData ObjectData_Field
    ArrayList<VisualParam> VisualParam_Fields = ArrayList<>()
    ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Int SerialNum
        UUID SessionID
        LLVector3 Size
    }

    class ObjectData {
        ByteArray TextureEntry
    }

    class VisualParam {
        Int ParamValue
    }

    class WearableData {
        UUID CacheID
        Int TextureIndex
    }

    AgentSetAppearance() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ObjectData_Field = ObjectData()
    }

    fun CalcPayloadSize(): Int {
        return (this.WearableData_Fields.size() * 17) + 53 + this.ObjectData_Field.TextureEntry.size + 2 + 1 + (this.VisualParam_Fields.size() * 1)
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAgentSetAppearance(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 84)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.SerialNum)
        packLLVector3(byteBuffer, this.AgentData_Field.Size)
        byteBuffer.put((this as byte).WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.CacheID)
            packByte(byteBuffer, (wearableData as byte).TextureIndex)
        }
        packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2)
        byteBuffer.put((this as byte).VisualParam_Fields.size())
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (visualParam as byte).ParamValue)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.SerialNum = unpackInt(byteBuffer)
        this.AgentData_Field.Size = unpackLLVector3(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            WearableData wearableData = WearableData()
            wearableData.CacheID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.WearableData_Fields.add(wearableData)
        }
        this.ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2)
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            VisualParam visualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.VisualParam_Fields.add(visualParam)
        }
    }
}
