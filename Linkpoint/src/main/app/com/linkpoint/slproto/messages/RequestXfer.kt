package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestXfer : SLMessage {
    XferID XferID_Field = XferID()

    class XferID {
        Boolean DeleteOnCompletion
        Int FilePath
        ByteArray Filename
        Long ID
        Boolean UseBigPackets
        UUID VFileID
        Int VFileType
    }

    RequestXfer() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.XferID_Field.Filename.size + 9 + 1 + 1 + 1 + 16 + 2 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRequestXfer(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -100)
        packLong(byteBuffer, this.XferID_Field.ID)
        packVariable(byteBuffer, this.XferID_Field.Filename, 1)
        packByte(byteBuffer, (this as Byte).XferID_Field.FilePath)
        packBoolean(byteBuffer, this.XferID_Field.DeleteOnCompletion)
        packBoolean(byteBuffer, this.XferID_Field.UseBigPackets)
        packUUID(byteBuffer, this.XferID_Field.VFileID)
        packShort(byteBuffer, (this as Short).XferID_Field.VFileType)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.XferID_Field.ID = unpackLong(byteBuffer)
        this.XferID_Field.Filename = unpackVariable(byteBuffer, 1)
        this.XferID_Field.FilePath = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.XferID_Field.DeleteOnCompletion = unpackBoolean(byteBuffer)
        this.XferID_Field.UseBigPackets = unpackBoolean(byteBuffer)
        this.XferID_Field.VFileID = unpackUUID(byteBuffer)
        this.XferID_Field.VFileType = unpackShort(byteBuffer)
    }
}
