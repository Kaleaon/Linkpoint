package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class DataHomeLocationReply : SLMessage() {
    public Info Info_Field = Info()

    @JvmStatic
    class Info {
        public UUID AgentID
        public LLVector3 LookAt
        public LLVector3 Position
        public Long RegionHandle
    }

    public DataHomeLocationReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 52
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataHomeLocationReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 68)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packLong(byteBuffer, this.Info_Field.RegionHandle)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.RegionHandle = unpackLong(byteBuffer)
        this.Info_Field.Position = unpackLLVector3(byteBuffer)
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
