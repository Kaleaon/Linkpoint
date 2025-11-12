package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ReportAutosaveCrashMessage : SLMessage() {
    var pid: Int = 0
    var status: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, pid)
        packInt(buffer, status)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        pid = unpackInt(buffer)
        status = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0080

    override fun getMessageName(): String = "ReportAutosaveCrash"
}
