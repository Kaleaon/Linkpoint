package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelPropertiesRequest : SLMessage() {
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
        public Int SequenceID
        public Boolean SnapSelection
        public Float South
        public Float West
    }

    public ParcelPropertiesRequest() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return 55
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelPropertiesRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put(Ascii.VT)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.SequenceID)
        packFloat(byteBuffer, this.ParcelData_Field.West)
        packFloat(byteBuffer, this.ParcelData_Field.South)
        packFloat(byteBuffer, this.ParcelData_Field.East)
        packFloat(byteBuffer, this.ParcelData_Field.North)
        packBoolean(byteBuffer, this.ParcelData_Field.SnapSelection)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer)
        this.ParcelData_Field.West = unpackFloat(byteBuffer)
        this.ParcelData_Field.South = unpackFloat(byteBuffer)
        this.ParcelData_Field.East = unpackFloat(byteBuffer)
        this.ParcelData_Field.North = unpackFloat(byteBuffer)
        this.ParcelData_Field.SnapSelection = unpackBoolean(byteBuffer)
    }
}
