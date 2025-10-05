package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestXfer : SLMessage() {
    val XferID_Field = XferID()

    class XferID {
        var ID: Long = 0
        lateinit var Filename: ByteArray
        var FilePath: Int = 0
        var DeleteOnCompletion: Boolean = false
        var UseBigPackets: Boolean = false
        var VFileID: UUID? = null
        var VFileType: Int = 0
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return XferID_Field.Filename.size + 9 + 1 + 1 + 1 + 16 + 2 + 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRequestXfer(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-100).toByte())
        packLong(buffer, XferID_Field.ID)
        packVariable(buffer, XferID_Field.Filename, 1)
        packByte(buffer, XferID_Field.FilePath.toByte())
        packBoolean(buffer, XferID_Field.DeleteOnCompletion)
        packBoolean(buffer, XferID_Field.UseBigPackets)
        packUUID(buffer, XferID_Field.VFileID)
        packShort(buffer, XferID_Field.VFileType.toShort())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        XferID_Field.ID = unpackLong(buffer)
        XferID_Field.Filename = unpackVariable(buffer, 1)
        XferID_Field.FilePath = unpackByte(buffer).toInt() and UnsignedBytes.MAX_VALUE.toInt()
        XferID_Field.DeleteOnCompletion = unpackBoolean(buffer)
        XferID_Field.UseBigPackets = unpackBoolean(buffer)
        XferID_Field.VFileID = unpackUUID(buffer)
        XferID_Field.VFileType = unpackShort(buffer).toInt()
    }
}