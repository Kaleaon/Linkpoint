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

    Int CalcPayloadSize() {
        return 24
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferAbort(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -101)
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID)
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer)
    }
}
