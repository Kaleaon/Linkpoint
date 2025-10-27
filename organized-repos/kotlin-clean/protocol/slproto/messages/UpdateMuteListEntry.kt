package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateMuteListEntry : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public MuteData MuteData_Field = MuteData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class MuteData {
        public Int MuteFlags
        public UUID MuteID
        public ByteArray MuteName
        public Int MuteType
    }

    public UpdateMuteListEntry() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.MuteData_Field.MuteName.length + 17 + 4 + 4 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateMuteListEntry(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 7)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.MuteData_Field.MuteID)
        packVariable(byteBuffer, this.MuteData_Field.MuteName, 1)
        packInt(byteBuffer, this.MuteData_Field.MuteType)
        packInt(byteBuffer, this.MuteData_Field.MuteFlags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteName = unpackVariable(byteBuffer, 1)
        this.MuteData_Field.MuteType = unpackInt(byteBuffer)
        this.MuteData_Field.MuteFlags = unpackInt(byteBuffer)
    }
}
