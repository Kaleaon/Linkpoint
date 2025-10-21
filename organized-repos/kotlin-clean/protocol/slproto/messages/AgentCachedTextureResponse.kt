package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AgentCachedTextureResponse : SLMessage() {
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
        public Byte[] HostName
        public UUID TextureID
        public Int TextureIndex
    }

    public AgentCachedTextureResponse() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
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

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentCachedTextureResponse(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
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

    fun UnpackPayload(ByteBuffer byteBuffer) {
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
