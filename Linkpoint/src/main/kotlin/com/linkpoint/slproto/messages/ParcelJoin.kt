package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelJoin : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ParcelData ParcelData_Field = ParcelData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ParcelData {
        public Float East
        public Float North
        public Float South
        public Float West
    }

    public ParcelJoin() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelJoin(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -46)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packFloat(byteBuffer, this.ParcelData_Field.West)
        packFloat(byteBuffer, this.ParcelData_Field.South)
        packFloat(byteBuffer, this.ParcelData_Field.East)
        packFloat(byteBuffer, this.ParcelData_Field.North)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.West = unpackFloat(byteBuffer)
        this.ParcelData_Field.South = unpackFloat(byteBuffer)
        this.ParcelData_Field.East = unpackFloat(byteBuffer)
        this.ParcelData_Field.North = unpackFloat(byteBuffer)
    }
}
