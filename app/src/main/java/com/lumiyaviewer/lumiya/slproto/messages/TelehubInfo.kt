package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class TelehubInfo : SLMessage {
    ArrayList<SpawnPointBlock> SpawnPointBlock_Fields = ArrayList<>()
    TelehubBlock TelehubBlock_Field

    class SpawnPointBlock {
        LLVector3 SpawnPointPos
    }

    class TelehubBlock {
        UUID ObjectID
        Byte[] ObjectName
        LLVector3 TelehubPos
        LLQuaternion TelehubRot
    }

    TelehubInfo() {
        this.zeroCoded = false
        this.TelehubBlock_Field = TelehubBlock()
    }

    Int CalcPayloadSize() {
        return this.TelehubBlock_Field.ObjectName.length + 17 + 12 + 12 + 4 + 1 + (this.SpawnPointBlock_Fields.size() * 12)
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTelehubInfo(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 10)
        packUUID(byteBuffer, this.TelehubBlock_Field.ObjectID)
        packVariable(byteBuffer, this.TelehubBlock_Field.ObjectName, 1)
        packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos)
        packLLQuaternion(byteBuffer, this.TelehubBlock_Field.TelehubRot)
        byteBuffer.put((Byte) this.SpawnPointBlock_Fields.size())
        for (SpawnPointBlock spawnPointBlock : this.SpawnPointBlock_Fields) {
            packLLVector3(byteBuffer, spawnPointBlock.SpawnPointPos)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TelehubBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.TelehubBlock_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.TelehubBlock_Field.TelehubPos = unpackLLVector3(byteBuffer)
        this.TelehubBlock_Field.TelehubRot = unpackLLQuaternion(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            SpawnPointBlock spawnPointBlock = SpawnPointBlock()
            spawnPointBlock.SpawnPointPos = unpackLLVector3(byteBuffer)
            this.SpawnPointBlock_Fields.add(spawnPointBlock)
        }
    }
}
