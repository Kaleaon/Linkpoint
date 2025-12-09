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

    fun CalcPayloadSize(): Int {
        return 28
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDataHomeLocationRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 67)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packInt(byteBuffer, this.Info_Field.KickedFromEstateID)
        packInt(byteBuffer, this.AgentInfo_Field.AgentEffectiveMaturity)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.KickedFromEstateID = unpackInt(byteBuffer)
        this.AgentInfo_Field.AgentEffectiveMaturity = unpackInt(byteBuffer)
    }
}
