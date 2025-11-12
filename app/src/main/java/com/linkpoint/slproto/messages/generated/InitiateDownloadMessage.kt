package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InitiateDownloadMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var simFilename: ByteArray = ByteArray(0)
    var viewerFilename: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packVariable(buffer, simFilename, 1)
        packVariable(buffer, viewerFilename, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        simFilename = unpackVariable(buffer, 1)
        viewerFilename = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF0193

    override fun getMessageName(): String = "InitiateDownload"
}
