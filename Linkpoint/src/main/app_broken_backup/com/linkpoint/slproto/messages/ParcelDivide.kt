package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDivide : SLMessage {
    AgentData AgentData_Field = AgentData()
    ParcelData ParcelData_Field = ParcelData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ParcelData {
        Float East
        Float North
        Float South
        Float West
    }

    ParcelDivide() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelDivide(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -45)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packFloat(byteBuffer, this.ParcelData_Field.West)
        packFloat(byteBuffer, this.ParcelData_Field.South)
        packFloat(byteBuffer, this.ParcelData_Field.East)
        packFloat(byteBuffer, this.ParcelData_Field.North)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.West = unpackFloat(byteBuffer)
        this.ParcelData_Field.South = unpackFloat(byteBuffer)
        this.ParcelData_Field.East = unpackFloat(byteBuffer)
        this.ParcelData_Field.North = unpackFloat(byteBuffer)
    }
}
