package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MuteListRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public MuteData MuteData_Field = MuteData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class MuteData {
        public Int MuteCRC
    }

    public MuteListRequest() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleMuteListRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 6)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.MuteData_Field.MuteCRC)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteCRC = unpackInt(byteBuffer)
    }
}
