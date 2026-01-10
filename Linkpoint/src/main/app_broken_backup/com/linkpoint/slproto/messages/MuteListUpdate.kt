package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MuteListUpdate : SLMessage {
    MuteData MuteData_Field = MuteData()

    class MuteData {
        UUID AgentID
        ByteArray Filename
    }

    MuteListUpdate() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.MuteData_Field.Filename.size + 17 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMuteListUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 62)
        packUUID(byteBuffer, this.MuteData_Field.AgentID)
        packVariable(byteBuffer, this.MuteData_Field.Filename, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.MuteData_Field.AgentID = unpackUUID(byteBuffer)
        this.MuteData_Field.Filename = unpackVariable(byteBuffer, 1)
    }
}
