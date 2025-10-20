package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarNotesReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class Data {
        public Byte[] Notes
        public UUID TargetID
    }

    public AvatarNotesReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.Notes.length + 18 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarNotesReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -80)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.Data_Field.TargetID)
        packVariable(byteBuffer, this.Data_Field.Notes, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.TargetID = unpackUUID(byteBuffer)
        this.Data_Field.Notes = unpackVariable(byteBuffer, 2)
    }
}
