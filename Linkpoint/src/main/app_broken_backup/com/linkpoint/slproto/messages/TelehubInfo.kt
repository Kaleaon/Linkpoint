package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
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
        ByteArray ObjectName
        LLVector3 TelehubPos
        LLQuaternion TelehubRot
    }

    TelehubInfo() {
        this.zeroCoded = false
        this.TelehubBlock_Field = TelehubBlock()
    }

    fun CalcPayloadSize(): Int {
        return this.TelehubBlock_Field.ObjectName.size + 17 + 12 + 12 + 4 + 1 + (this.SpawnPointBlock_Fields.size() * 12)
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTelehubInfo(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 10)
        packUUID(byteBuffer, this.TelehubBlock_Field.ObjectID)
        packVariable(byteBuffer, this.TelehubBlock_Field.ObjectName, 1)
        packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos)
        packLLQuaternion(byteBuffer, this.TelehubBlock_Field.TelehubRot)
        byteBuffer.put((this as Byte).SpawnPointBlock_Fields.size())
        for (SpawnPointBlock spawnPointBlock : this.SpawnPointBlock_Fields) {
            packLLVector3(byteBuffer, spawnPointBlock.SpawnPointPos)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TelehubBlock_Field.ObjectID = unpackUUID(byteBuffer)
        this.TelehubBlock_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.TelehubBlock_Field.TelehubPos = unpackLLVector3(byteBuffer)
        this.TelehubBlock_Field.TelehubRot = unpackLLQuaternion(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            SpawnPointBlock spawnPointBlock = SpawnPointBlock()
            spawnPointBlock.SpawnPointPos = unpackLLVector3(byteBuffer)
            this.SpawnPointBlock_Fields.add(spawnPointBlock)
        }
    }
}
