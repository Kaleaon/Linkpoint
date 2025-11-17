package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DataHomeLocationRequest : SLMessage {
    AgentInfo AgentInfo_Field = AgentInfo()
    Info Info_Field = Info()

    class AgentInfo {
        Int AgentEffectiveMaturity
    }

    class Info {
        UUID AgentID
        Int KickedFromEstateID
    }

    DataHomeLocationRequest() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 28
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataHomeLocationRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 67)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packInt(byteBuffer, this.Info_Field.KickedFromEstateID)
        packInt(byteBuffer, this.AgentInfo_Field.AgentEffectiveMaturity)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.KickedFromEstateID = unpackInt(byteBuffer)
        this.AgentInfo_Field.AgentEffectiveMaturity = unpackInt(byteBuffer)
    }
}
