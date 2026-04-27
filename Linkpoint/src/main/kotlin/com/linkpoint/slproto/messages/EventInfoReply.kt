package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class EventInfoReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public EventData EventData_Field = EventData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class EventData {
        public Int Amount
        public ByteArray Category
        public Int Cover
        public ByteArray Creator
        public ByteArray Date
        public Int DateUTC
        public ByteArray Desc
        public Int Duration
        public Int EventFlags
        public Int EventID
        public LLVector3d GlobalPos
        public ByteArray Name
        public ByteArray SimName
    }

    public EventInfoReply() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return this.EventData_Field.Creator.length + 5 + 1 + this.EventData_Field.Name.length + 1 + this.EventData_Field.Category.length + 2 + this.EventData_Field.Desc.length + 1 + this.EventData_Field.Date.length + 4 + 4 + 4 + 4 + 1 + this.EventData_Field.SimName.length + 24 + 4 + 20
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleEventInfoReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -76)
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
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
