package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferRequest : SLMessage {
    TransferInfo TransferInfo_Field = TransferInfo()

    class TransferInfo {
        Int ChannelType
        ByteArray Params
        Float Priority
        Int SourceType
        UUID TransferID
    }

    TransferRequest() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.TransferInfo_Field.Params.length + 30 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -103)
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID)
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType)
        packInt(byteBuffer, this.TransferInfo_Field.SourceType)
        packFloat(byteBuffer, this.TransferInfo_Field.Priority)
        packVariable(byteBuffer, this.TransferInfo_Field.Params, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer)
        this.TransferInfo_Field.SourceType = unpackInt(byteBuffer)
        this.TransferInfo_Field.Priority = unpackFloat(byteBuffer)
        this.TransferInfo_Field.Params = unpackVariable(byteBuffer, 2)
    }
}
