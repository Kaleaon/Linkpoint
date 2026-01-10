package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartAuction : SLMessage {
    AgentData AgentData_Field = AgentData()
    ParcelData ParcelData_Field = ParcelData()

    class AgentData {
        UUID AgentID
    }

    class ParcelData {
        ByteArray Name
        UUID ParcelID
        UUID SnapshotID
    }

    StartAuction() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.ParcelData_Field.Name.size + 33 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleStartAuction(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -27)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.ParcelData_Field.ParcelID)
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID)
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.ParcelData_Field.ParcelID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer)
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
