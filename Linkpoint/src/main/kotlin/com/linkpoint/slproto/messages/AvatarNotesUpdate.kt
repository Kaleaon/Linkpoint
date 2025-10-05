package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarNotesUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Byte[] Notes
        public UUID TargetID
    }

    public AvatarNotesUpdate() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.Notes.length + 18 + 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarNotesUpdate(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -79)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.TargetID)
        packVariable(byteBuffer, this.Data_Field.Notes, 2)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.TargetID = unpackUUID(byteBuffer)
        this.Data_Field.Notes = unpackVariable(byteBuffer, 2)
    }
}
