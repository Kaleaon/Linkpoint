package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarTextureUpdate : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<TextureData> TextureData_Fields = ArrayList<>()
    public ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Boolean TexturesChanged
    }

    @JvmStatic
    class TextureData {
        public UUID TextureID
    }

    @JvmStatic
    class WearableData {
        public UUID CacheID
        public ByteArray HostName
        public Int TextureIndex
    }

    public AvatarTextureUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 22
        val it: Iterator<T> = this.WearableData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.TextureData_Fields.size() * 16)
            }
            i = ((WearableData) it.next()).HostName.length + 18 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarTextureUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 4)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packBoolean(byteBuffer, this.AgentData_Field.TexturesChanged)
        byteBuffer.put((Byte) this.WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.CacheID)
            packByte(byteBuffer, (Byte) wearableData.TextureIndex)
            packVariable(byteBuffer, wearableData.HostName, 1)
        }
        byteBuffer.put((Byte) this.TextureData_Fields.size())
        for (TextureData textureData : this.TextureData_Fields) {
            packUUID(byteBuffer, textureData.TextureID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TexturesChanged = unpackBoolean(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val wearableData: WearableData = WearableData()
            wearableData.CacheID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            wearableData.HostName = unpackVariable(byteBuffer, 1)
            this.WearableData_Fields.add(wearableData)
        }
        val b2: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            val textureData: TextureData = TextureData()
            textureData.TextureID = unpackUUID(byteBuffer)
            this.TextureData_Fields.add(textureData)
        }
    }
}
