package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.textures.MutableSLTextureEntryFace
import java.nio.ByteBuffer
import java.util.UUID

class ForceScriptControlRelease : SLMessage() {
    public AgentData AgentData_Field = AgentData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    public ForceScriptControlRelease() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleForceScriptControlRelease(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(MutableSLTextureEntryFace.SHINY_MASK)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
    }
}
