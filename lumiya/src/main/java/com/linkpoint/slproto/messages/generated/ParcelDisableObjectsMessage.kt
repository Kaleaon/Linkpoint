package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDisableObjectsMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    var returnType: Int = 0
    val taskIDs: MutableList<TaskIDsBlock> = mutableListOf()
    val ownerIDs: MutableList<OwnerIDsBlock> = mutableListOf()

    data class TaskIDsBlock(
        var taskId: UUID = UUID(0L, 0L)
    )
    data class OwnerIDsBlock(
        var ownerId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, localId)
        packInt(buffer, returnType)
        require(taskIDs.size <= 0xFF) { "TaskIDs size exceeds 255 (" + taskIDs.size + ")" }
        packByte(buffer, taskIDs.size)
        taskIDs.forEach { entry ->
            packUUID(buffer, entry.taskId)
        }
        require(ownerIDs.size <= 0xFF) { "OwnerIDs size exceeds 255 (" + ownerIDs.size + ")" }
        packByte(buffer, ownerIDs.size)
        ownerIDs.forEach { entry ->
            packUUID(buffer, entry.ownerId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        returnType = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            taskIDs.clear()
            repeat(count) {
                val entry = TaskIDsBlock()
                entry.taskId = unpackUUID(buffer)
                taskIDs += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            ownerIDs.clear()
            repeat(count) {
                val entry = OwnerIDsBlock()
                entry.ownerId = unpackUUID(buffer)
                ownerIDs += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00C9

    override fun getMessageName(): String = "ParcelDisableObjects"
}
