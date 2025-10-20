package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ViewerStartAuction : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ParcelData ParcelData_Field = ParcelData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ParcelData {
        public Int LocalID
        public UUID SnapshotID
    }

    public ViewerStartAuction() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 56
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerStartAuction(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -28)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer)
    }
}
