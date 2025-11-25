package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class BuyObjectInventory : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Field: Data = Data()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class Data {
        var FolderID: UUID? = null
        var ItemID: UUID? = null
        var OwnerID: UUID? = null
        var SalePrice: Int = 0
        var TransactionID: UUID? = null
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 84
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleBuyObjectInventory(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-86).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.SessionID!!)
        packUUID(byteBuffer, Data_Field.ItemID!!)
        packUUID(byteBuffer, Data_Field.FolderID!!)
        packUUID(byteBuffer, Data_Field.OwnerID!!)
        packUUID(byteBuffer, Data_Field.TransactionID!!)
        packInt(byteBuffer, Data_Field.SalePrice)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Data_Field.ItemID = unpackUUID(byteBuffer)
        Data_Field.FolderID = unpackUUID(byteBuffer)
        Data_Field.OwnerID = unpackUUID(byteBuffer)
        Data_Field.TransactionID = unpackUUID(byteBuffer)
        Data_Field.SalePrice = unpackInt(byteBuffer)
    }
}
