package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentSetAppearance : SLMessage() {
    public AgentData AgentData_Field
    public ObjectData ObjectData_Field
    public ArrayList<VisualParam> VisualParam_Fields = ArrayList<>()
    public ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int SerialNum
        public UUID SessionID
        public LLVector3 Size
    }

    @JvmStatic
    class ObjectData {
        public Byte[] TextureEntry
    }

    @JvmStatic
    class VisualParam {
        public Int ParamValue
    }

    @JvmStatic
    class WearableData {
        public UUID CacheID
        public Int TextureIndex
    }

    public AgentSetAppearance() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ObjectData_Field = ObjectData()
    }

    public Int CalcPayloadSize() {
        return (this.WearableData_Fields.size() * 17) + 53 + this.ObjectData_Field.TextureEntry.length + 2 + 1 + (this.VisualParam_Fields.size() * 1)
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentSetAppearance(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 84)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.SerialNum)
        packLLVector3(byteBuffer, this.AgentData_Field.Size)
        byteBuffer.put((Byte) this.WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.CacheID)
            packByte(byteBuffer, (Byte) wearableData.TextureIndex)
        }
        packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2)
        byteBuffer.put((Byte) this.VisualParam_Fields.size())
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (Byte) visualParam.ParamValue)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.SerialNum = unpackInt(byteBuffer)
        this.AgentData_Field.Size = unpackLLVector3(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            WearableData wearableData = WearableData()
            wearableData.CacheID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.WearableData_Fields.add(wearableData)
        }
        this.ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2)
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            VisualParam visualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.VisualParam_Fields.add(visualParam)
        }
    }
}
