package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DataHomeLocationRequest : SLMessage() {
    public AgentInfo AgentInfo_Field = AgentInfo()
    public Info Info_Field = Info()

    @JvmStatic
    class AgentInfo {
        public Int AgentEffectiveMaturity
    }

    @JvmStatic
    class Info {
        public UUID AgentID
        public Int KickedFromEstateID
    }

    public DataHomeLocationRequest() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 28
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataHomeLocationRequest(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 67)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packInt(byteBuffer, this.Info_Field.KickedFromEstateID)
        packInt(byteBuffer, this.AgentInfo_Field.AgentEffectiveMaturity)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.KickedFromEstateID = unpackInt(byteBuffer)
        this.AgentInfo_Field.AgentEffectiveMaturity = unpackInt(byteBuffer)
    }
}
