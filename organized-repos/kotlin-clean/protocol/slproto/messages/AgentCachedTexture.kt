package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentCachedTexture : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int SerialNum
        public UUID SessionID
    }

    @JvmStatic
    class WearableData {
        public UUID ID
        public Int TextureIndex
    }

    public AgentCachedTexture() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        return (this.WearableData_Fields.size() * 17) + 41
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentCachedTexture(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Byte.MIN_VALUE)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.SerialNum)
        byteBuffer.put((Byte) this.WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.ID)
            packByte(byteBuffer, (Byte) wearableData.TextureIndex)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.SerialNum = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            WearableData wearableData = WearableData()
            wearableData.ID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.WearableData_Fields.add(wearableData)
        }
    }
}
