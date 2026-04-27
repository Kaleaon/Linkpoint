package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferPacket : SLMessage() {
    public TransferData TransferData_Field = TransferData()

    @JvmStatic
    class TransferData {
        public Int ChannelType
        public ByteArray Data
        public Int Packet
        public Int Status
        public UUID TransferID
    }

    public TransferPacket() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.TransferData_Field.Data.length + 30 + 1
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferPacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 17)
        packUUID(byteBuffer, this.TransferData_Field.TransferID)
        packInt(byteBuffer, this.TransferData_Field.ChannelType)
        packInt(byteBuffer, this.TransferData_Field.Packet)
        packInt(byteBuffer, this.TransferData_Field.Status)
        packVariable(byteBuffer, this.TransferData_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferData_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferData_Field.ChannelType = unpackInt(byteBuffer)
        this.TransferData_Field.Packet = unpackInt(byteBuffer)
        this.TransferData_Field.Status = unpackInt(byteBuffer)
        this.TransferData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
