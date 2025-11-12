package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestXferMessage : SLMessage() {
    var id: Long = 0L
    var filename: ByteArray = ByteArray(0)
    var filePath: Int = 0
    var deleteOnCompletion: Boolean = false
    var useBigPackets: Boolean = false
    var vFileId: UUID = UUID(0L, 0L)
    var vFileType: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, id)
        packVariable(buffer, filename, 1)
        packByte(buffer, filePath)
        packBoolean(buffer, deleteOnCompletion)
        packBoolean(buffer, useBigPackets)
        packUUID(buffer, vFileId)
        packShort(buffer, vFileType)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        id = unpackLong(buffer)
        filename = unpackVariable(buffer, 1)
        filePath = unpackByte(buffer)
        deleteOnCompletion = unpackBoolean(buffer)
        useBigPackets = unpackBoolean(buffer)
        vFileId = unpackUUID(buffer)
        vFileType = unpackShort(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF009C

    override fun getMessageName(): String = "RequestXfer"
}
