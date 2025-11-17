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

    Int CalcPayloadSize() {
        return this.XferID_Field.Filename.length + 9 + 1 + 1 + 1 + 16 + 2 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestXfer(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -100)
        packLong(byteBuffer, this.XferID_Field.ID)
        packVariable(byteBuffer, this.XferID_Field.Filename, 1)
        packByte(byteBuffer, (Byte) this.XferID_Field.FilePath)
        packBoolean(byteBuffer, this.XferID_Field.DeleteOnCompletion)
        packBoolean(byteBuffer, this.XferID_Field.UseBigPackets)
        packUUID(byteBuffer, this.XferID_Field.VFileID)
        packShort(byteBuffer, (Short) this.XferID_Field.VFileType)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer)
        this.XferID_Field.Filename = unpackVariable(byteBuffer, 1)
        this.XferID_Field.FilePath = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.XferID_Field.DeleteOnCompletion = unpackBoolean(byteBuffer)
        this.XferID_Field.UseBigPackets = unpackBoolean(byteBuffer)
        this.XferID_Field.VFileID = unpackUUID(byteBuffer)
        this.XferID_Field.VFileType = unpackShort(byteBuffer)
    }
}
