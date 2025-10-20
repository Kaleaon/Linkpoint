package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DirPopularQueryBackend : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public QueryData QueryData_Field = QueryData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class QueryData {
        public Int EstateID
        public Boolean Godlike
        public Int QueryFlags
        public UUID QueryID
    }

    public DirPopularQueryBackend() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 45
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPopularQueryBackend(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 52)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.EstateID)
        packBoolean(byteBuffer, this.QueryData_Field.Godlike)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.EstateID = unpackInt(byteBuffer)
        this.QueryData_Field.Godlike = unpackBoolean(byteBuffer)
    }
}
