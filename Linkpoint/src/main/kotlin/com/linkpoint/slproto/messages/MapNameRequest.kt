package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapNameRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public NameData NameData_Field = NameData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int EstateID
        public Int Flags
        public Boolean Godlike
        public UUID SessionID
    }

    @JvmStatic
    class NameData {
        public Byte[] Name
    }

    public MapNameRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.NameData_Field.Name.length + 1 + 45
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapNameRequest(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -104)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.AgentData_Field.EstateID)
        packBoolean(byteBuffer, this.AgentData_Field.Godlike)
        packVariable(byteBuffer, this.NameData_Field.Name, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.AgentData_Field.EstateID = unpackInt(byteBuffer)
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer)
        this.NameData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
