package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DirClassifiedQuery : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public QueryData QueryData_Field = QueryData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class QueryData {
        public Int Category
        public Int QueryFlags
        public UUID QueryID
        public Int QueryStart
        public Byte[] QueryText
    }

    public DirClassifiedQuery() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.QueryData_Field.QueryText.length + 17 + 4 + 4 + 4 + 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirClassifiedQuery(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 39)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.Category)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.Category = unpackInt(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
    }
}
