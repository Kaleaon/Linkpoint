package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PlacesQuery : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public QueryData QueryData_Field = QueryData()
    public TransactionData TransactionData_Field = TransactionData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID QueryID
        public UUID SessionID
    }

    @JvmStatic
    class QueryData {
        public Int Category
        public Int QueryFlags
        public ByteArray QueryText
        public ByteArray SimName
    }

    @JvmStatic
    class TransactionData {
        public UUID TransactionID
    }

    public PlacesQuery() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.QueryData_Field.QueryText.length + 1 + 4 + 1 + 1 + this.QueryData_Field.SimName.length + 68
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePlacesQuery(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.GS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packByte(byteBuffer, (Byte) this.QueryData_Field.Category)
        packVariable(byteBuffer, this.QueryData_Field.SimName, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
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
