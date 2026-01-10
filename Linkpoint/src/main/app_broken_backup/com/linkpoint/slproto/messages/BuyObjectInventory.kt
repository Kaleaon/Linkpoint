package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class BuyObjectInventory : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        UUID FolderID
        UUID ItemID
        UUID ObjectID
    }

    BuyObjectInventory() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 84
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleBuyObjectInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 103)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.ObjectID)
        packUUID(byteBuffer, this.Data_Field.ItemID)
        packUUID(byteBuffer, this.Data_Field.FolderID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.ItemID = unpackUUID(byteBuffer)
        this.Data_Field.FolderID = unpackUUID(byteBuffer)
    }
}
