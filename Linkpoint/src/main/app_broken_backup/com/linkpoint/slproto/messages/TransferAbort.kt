package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferAbort : SLMessage {
    TransferInfo TransferInfo_Field = TransferInfo()

    class TransferInfo {
        Int ChannelType
        UUID TransferID
    }

    TransferAbort() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 24
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTransferAbort(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -101)
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID)
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer)
    }
}
