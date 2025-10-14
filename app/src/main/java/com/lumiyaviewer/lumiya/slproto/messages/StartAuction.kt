package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartAuction : SLMessage {
    AgentData AgentData_Field = AgentData()
    ParcelData ParcelData_Field = ParcelData()

    class AgentData {
        UUID AgentID
    }

    class ParcelData {
        Byte[] Name
        UUID ParcelID
        UUID SnapshotID
    }

    StartAuction() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 33 + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartAuction(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -27)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.ParcelData_Field.ParcelID)
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID)
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.ParcelData_Field.ParcelID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer)
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
