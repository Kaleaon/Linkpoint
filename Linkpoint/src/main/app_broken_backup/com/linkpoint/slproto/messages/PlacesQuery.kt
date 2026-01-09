package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PlacesQuery : SLMessage {
    AgentData AgentData_Field = AgentData()
    QueryData QueryData_Field = QueryData()
    TransactionData TransactionData_Field = TransactionData()

    class AgentData {
        UUID AgentID
        UUID QueryID
        UUID SessionID
    }

    class QueryData {
        Int Category
        Int QueryFlags
        ByteArray QueryText
        ByteArray SimName
    }

    class TransactionData {
        UUID TransactionID
    }

    PlacesQuery() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.QueryData_Field.QueryText.size + 1 + 4 + 1 + 1 + this.QueryData_Field.SimName.size + 68
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandlePlacesQuery(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.GS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packByte(byteBuffer, (this as Byte).QueryData_Field.Category)
        packVariable(byteBuffer, this.QueryData_Field.SimName, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.Category = unpackByte(byteBuffer)
        this.QueryData_Field.SimName = unpackVariable(byteBuffer, 1)
    }
}
