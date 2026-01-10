package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class EventInfoReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    EventData EventData_Field = EventData()

    class AgentData {
        UUID AgentID
    }

    class EventData {
        Int Amount
        ByteArray Category
        Int Cover
        ByteArray Creator
        ByteArray Date
        Int DateUTC
        ByteArray Desc
        Int Duration
        Int EventFlags
        Int EventID
        LLVector3d GlobalPos
        ByteArray Name
        ByteArray SimName
    }

    EventInfoReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.EventData_Field.Creator.size + 5 + 1 + this.EventData_Field.Name.size + 1 + this.EventData_Field.Category.size + 2 + this.EventData_Field.Desc.size + 1 + this.EventData_Field.Date.size + 4 + 4 + 4 + 4 + 1 + this.EventData_Field.SimName.size + 24 + 4 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleEventInfoReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -76)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.EventData_Field.EventID)
        packVariable(byteBuffer, this.EventData_Field.Creator, 1)
        packVariable(byteBuffer, this.EventData_Field.Name, 1)
        packVariable(byteBuffer, this.EventData_Field.Category, 1)
        packVariable(byteBuffer, this.EventData_Field.Desc, 2)
        packVariable(byteBuffer, this.EventData_Field.Date, 1)
        packInt(byteBuffer, this.EventData_Field.DateUTC)
        packInt(byteBuffer, this.EventData_Field.Duration)
        packInt(byteBuffer, this.EventData_Field.Cover)
        packInt(byteBuffer, this.EventData_Field.Amount)
        packVariable(byteBuffer, this.EventData_Field.SimName, 1)
        packLLVector3d(byteBuffer, this.EventData_Field.GlobalPos)
        packInt(byteBuffer, this.EventData_Field.EventFlags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
        this.EventData_Field.Creator = unpackVariable(byteBuffer, 1)
        this.EventData_Field.Name = unpackVariable(byteBuffer, 1)
        this.EventData_Field.Category = unpackVariable(byteBuffer, 1)
        this.EventData_Field.Desc = unpackVariable(byteBuffer, 2)
        this.EventData_Field.Date = unpackVariable(byteBuffer, 1)
        this.EventData_Field.DateUTC = unpackInt(byteBuffer)
        this.EventData_Field.Duration = unpackInt(byteBuffer)
        this.EventData_Field.Cover = unpackInt(byteBuffer)
        this.EventData_Field.Amount = unpackInt(byteBuffer)
        this.EventData_Field.SimName = unpackVariable(byteBuffer, 1)
        this.EventData_Field.GlobalPos = unpackLLVector3d(byteBuffer)
        this.EventData_Field.EventFlags = unpackInt(byteBuffer)
    }
}
