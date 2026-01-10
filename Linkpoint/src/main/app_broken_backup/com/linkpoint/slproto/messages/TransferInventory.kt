package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class TransferInventory : SLMessage {
    InfoBlock InfoBlock_Field
    ArrayList<InventoryBlock> InventoryBlock_Fields = ArrayList<>()

    class InfoBlock {
        UUID DestID
        UUID SourceID
        UUID TransactionID
    }

    class InventoryBlock {
        UUID InventoryID
        Int Type
    }

    TransferInventory() {
        this.zeroCoded = true
        this.InfoBlock_Field = InfoBlock()
    }

    fun CalcPayloadSize(): Int {
        return (this.InventoryBlock_Fields.size() * 17) + 53
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTransferInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 39)
        packUUID(byteBuffer, this.InfoBlock_Field.SourceID)
        packUUID(byteBuffer, this.InfoBlock_Field.DestID)
        packUUID(byteBuffer, this.InfoBlock_Field.TransactionID)
        byteBuffer.put((this as Byte).InventoryBlock_Fields.size())
        for (InventoryBlock inventoryBlock : this.InventoryBlock_Fields) {
            packUUID(byteBuffer, inventoryBlock.InventoryID)
            packByte(byteBuffer, (inventoryBlock as Byte).Type)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.InfoBlock_Field.SourceID = unpackUUID(byteBuffer)
        this.InfoBlock_Field.DestID = unpackUUID(byteBuffer)
        this.InfoBlock_Field.TransactionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            InventoryBlock inventoryBlock = InventoryBlock()
            inventoryBlock.InventoryID = unpackUUID(byteBuffer)
            inventoryBlock.Type = unpackByte(byteBuffer)
            this.InventoryBlock_Fields.add(inventoryBlock)
        }
    }
}
