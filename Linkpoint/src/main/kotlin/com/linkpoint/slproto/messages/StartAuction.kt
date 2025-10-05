package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartAuction : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ParcelData ParcelData_Field = ParcelData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class ParcelData {
        public Byte[] Name
        public UUID ParcelID
        public UUID SnapshotID
    }

    public StartAuction() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 33 + 20
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartAuction(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -27)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.ParcelData_Field.ParcelID)
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID)
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.ParcelData_Field.ParcelID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer)
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
