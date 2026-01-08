package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferPacket : SLMessage {
    TransferData TransferData_Field = TransferData()

    class TransferData {
        Int ChannelType
        ByteArray Data
        Int Packet
        Int Status
        UUID TransferID
    }

    TransferPacket() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.TransferData_Field.Data.size + 30 + 1
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTransferPacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put((Byte) 17)
        packUUID(byteBuffer, this.TransferData_Field.TransferID)
        packInt(byteBuffer, this.TransferData_Field.ChannelType)
        packInt(byteBuffer, this.TransferData_Field.Packet)
        packInt(byteBuffer, this.TransferData_Field.Status)
        packVariable(byteBuffer, this.TransferData_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TransferData_Field.TransferID = unpackUUID(byteBuffer)
        this.TransferData_Field.ChannelType = unpackInt(byteBuffer)
        this.TransferData_Field.Packet = unpackInt(byteBuffer)
        this.TransferData_Field.Status = unpackInt(byteBuffer)
        this.TransferData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
