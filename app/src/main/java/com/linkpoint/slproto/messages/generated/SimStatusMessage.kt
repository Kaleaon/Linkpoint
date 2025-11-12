package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimStatusMessage : SLMessage() {
    var canAcceptAgents: Boolean = false
    var canAcceptTasks: Boolean = false
    var flags: Long = 0L


    override fun packPayload(buffer: ByteBuffer) {
        packBoolean(buffer, canAcceptAgents)
        packBoolean(buffer, canAcceptTasks)
        packLong(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        canAcceptAgents = unpackBoolean(buffer)
        canAcceptTasks = unpackBoolean(buffer)
        flags = unpackLong(buffer)
    }

    override fun getMessageID(): Int = 0x0000000C

    override fun getMessageName(): String = "SimStatus"
}
