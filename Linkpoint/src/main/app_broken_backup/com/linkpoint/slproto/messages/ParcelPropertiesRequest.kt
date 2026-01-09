package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelPropertiesRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    ParcelData ParcelData_Field = ParcelData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ParcelData {
        Float East
        Float North
        Int SequenceID
        Boolean SnapSelection
        Float South
        Float West
    }

    ParcelPropertiesRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 55
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelPropertiesRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
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

    fun UnpackPayload(ByteBuffer byteBuffer)  {
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
