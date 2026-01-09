package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return this.TransferInfo_Field.Params.size + 30 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTransferRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -103)
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID)
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType)
        packInt(byteBuffer, this.TransferInfo_Field.SourceType)
        packFloat(byteBuffer, this.TransferInfo_Field.Priority)
        packVariable(byteBuffer, this.TransferInfo_Field.Params, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer)
        this.TransferInfo_Field.SourceType = unpackInt(byteBuffer)
        this.TransferInfo_Field.Priority = unpackFloat(byteBuffer)
        this.TransferInfo_Field.Params = unpackVariable(byteBuffer, 2)
    }
}
