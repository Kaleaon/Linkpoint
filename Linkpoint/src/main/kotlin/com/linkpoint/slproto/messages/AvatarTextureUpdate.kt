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
        public Byte[] HostName
        public Int TextureIndex
    }

    public AvatarTextureUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int i = 22
        Iterator<T> it = this.WearableData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.TextureData_Fields.size() * 16)
            }
            i = ((WearableData) it.next()).HostName.length + 18 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarTextureUpdate(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TexturesChanged = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            WearableData wearableData = WearableData()
            wearableData.CacheID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            wearableData.HostName = unpackVariable(byteBuffer, 1)
            this.WearableData_Fields.add(wearableData)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            TextureData textureData = TextureData()
            textureData.TextureID = unpackUUID(byteBuffer)
            this.TextureData_Fields.add(textureData)
        }
    }
}
