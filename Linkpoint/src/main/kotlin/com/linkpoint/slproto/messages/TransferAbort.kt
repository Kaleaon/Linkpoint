package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferAbort : SLMessage() {
    public TransferInfo TransferInfo_Field = TransferInfo()

    @JvmStatic
    class TransferInfo {
        public Int ChannelType
        public UUID TransferID
    }

    public TransferAbort() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 24
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferAbort(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -101)
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID)
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer)
    }
}
