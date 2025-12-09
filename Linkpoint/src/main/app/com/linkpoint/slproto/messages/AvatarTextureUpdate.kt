package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarTextureUpdate : SLMessage {
    AgentData AgentData_Field
    ArrayList<TextureData> TextureData_Fields = ArrayList<>()
    ArrayList<WearableData> WearableData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Boolean TexturesChanged
    }

    class TextureData {
        UUID TextureID
    }

    class WearableData {
        UUID CacheID
        ByteArray HostName
        Int TextureIndex
    }

    AvatarTextureUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 22
        Iterator<T> it = this.WearableData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.TextureData_Fields.size() * 16)
            }
            i = ((it as WearableData).next()).HostName.size + 18 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAvatarTextureUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 4)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packBoolean(byteBuffer, this.AgentData_Field.TexturesChanged)
        byteBuffer.put((this as byte).WearableData_Fields.size())
        for (WearableData wearableData : this.WearableData_Fields) {
            packUUID(byteBuffer, wearableData.CacheID)
            packByte(byteBuffer, (wearableData as byte).TextureIndex)
            packVariable(byteBuffer, wearableData.HostName, 1)
        }
        byteBuffer.put((this as byte).TextureData_Fields.size())
        for (TextureData textureData : this.TextureData_Fields) {
            packUUID(byteBuffer, textureData.TextureID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TexturesChanged = unpackBoolean(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            WearableData wearableData = WearableData()
            wearableData.CacheID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            wearableData.HostName = unpackVariable(byteBuffer, 1)
            this.WearableData_Fields.add(wearableData)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            TextureData textureData = TextureData()
            textureData.TextureID = unpackUUID(byteBuffer)
            this.TextureData_Fields.add(textureData)
        }
    }
}
