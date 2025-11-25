package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class TelehubInfoMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var objectName: ByteArray = ByteArray(0)
    var telehubPos: LLVector3 = LLVector3()
    var telehubRot: LLQuaternion = LLQuaternion()
    val spawnPointBlock: MutableList<SpawnPointBlock> = mutableListOf()

    data class SpawnPointBlock(
        var spawnPointPos: LLVector3 = LLVector3()
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packVariable(buffer, objectName, 1)
        telehubPos.pack(buffer)
        telehubRot.pack(buffer)
        require(spawnPointBlock.size <= 0xFF) { "SpawnPointBlock size exceeds 255 (" + spawnPointBlock.size + ")" }
        packByte(buffer, spawnPointBlock.size)
        spawnPointBlock.forEach { entry ->
            entry.spawnPointPos.pack(buffer)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        objectName = unpackVariable(buffer, 1)
        telehubPos = LLVector3.unpack(buffer)
        telehubRot = LLQuaternion.unpack(buffer)
        run {
            val count = unpackByte(buffer)
            spawnPointBlock.clear()
            repeat(count) {
                val entry = SpawnPointBlock()
                entry.spawnPointPos = LLVector3.unpack(buffer)
                spawnPointBlock += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF000A.toInt()

    override fun getMessageName(): String = "TelehubInfo"
}
