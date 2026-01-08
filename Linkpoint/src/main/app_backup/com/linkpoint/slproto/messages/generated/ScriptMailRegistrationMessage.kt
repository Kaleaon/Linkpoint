package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptMailRegistrationMessage : SLMessage() {
    var targetIp: ByteArray = ByteArray(0)
    var targetPort: Int = 0
    var taskId: UUID = UUID(0L, 0L)
    var flags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, targetIp, 1)
        packUInt16(buffer, targetPort)
        packUUID(buffer, taskId)
        packInt(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        targetIp = unpackVariable(buffer, 1)
        targetPort = unpackUInt16(buffer)
        taskId = unpackUUID(buffer)
        flags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF01A2.toInt()

    override fun getMessageName(): String = "ScriptMailRegistration"
}
