package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferRequest : SLMessage() {
    public TransferInfo TransferInfo_Field = TransferInfo()

    @JvmStatic
    class TransferInfo {
        public Int ChannelType
        public Byte[] Params
        public Float Priority
        public Int SourceType
        public UUID TransferID
    }

    public TransferRequest() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.TransferInfo_Field.Params.length + 30 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -103)
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID)
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType)
        packInt(byteBuffer, this.TransferInfo_Field.SourceType)
        packFloat(byteBuffer, this.TransferInfo_Field.Priority)
        packVariable(byteBuffer, this.TransferInfo_Field.Params, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer)
        this.TransferInfo_Field.SourceType = unpackInt(byteBuffer)
        this.TransferInfo_Field.Priority = unpackFloat(byteBuffer)
        this.TransferInfo_Field.Params = unpackVariable(byteBuffer, 2)
    }
}
