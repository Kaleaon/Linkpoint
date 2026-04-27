package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferInfo : SLMessage() {
    public TransferInfoData TransferInfoData_Field = TransferInfoData()

    @JvmStatic
    class TransferInfoData {
        public Int ChannelType
        public ByteArray Params
        public Int Size
        public Int Status
        public Int TargetType
        public UUID TransferID
    }

    public TransferInfo() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.TransferInfoData_Field.Params.length + 34 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferInfo(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -102)
        packUUID(byteBuffer, this.TransferInfoData_Field.TransferID)
        packInt(byteBuffer, this.TransferInfoData_Field.ChannelType)
        packInt(byteBuffer, this.TransferInfoData_Field.TargetType)
        packInt(byteBuffer, this.TransferInfoData_Field.Status)
        packInt(byteBuffer, this.TransferInfoData_Field.Size)
        packVariable(byteBuffer, this.TransferInfoData_Field.Params, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfoData_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferInfoData_Field.ChannelType = unpackInt(byteBuffer)
        this.TransferInfoData_Field.TargetType = unpackInt(byteBuffer)
        this.TransferInfoData_Field.Status = unpackInt(byteBuffer)
        this.TransferInfoData_Field.Size = unpackInt(byteBuffer)
        this.TransferInfoData_Field.Params = unpackVariable(byteBuffer, 2)
    }
}
