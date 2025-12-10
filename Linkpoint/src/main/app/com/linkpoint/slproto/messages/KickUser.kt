package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class KickUser : SLMessage {
    TargetBlock TargetBlock_Field = TargetBlock()
    UserInfo UserInfo_Field = UserInfo()

    class TargetBlock {
        Inet4Address TargetIP
        Int TargetPort
    }

    class UserInfo {
        UUID AgentID
        ByteArray Reason
        UUID SessionID
    }

    KickUser() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.UserInfo_Field.Reason.size + 34 + 10
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleKickUser(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -93)
        packIPAddress(byteBuffer, this.TargetBlock_Field.TargetIP)
        packShort(byteBuffer, (this as short).TargetBlock_Field.TargetPort)
        packUUID(byteBuffer, this.UserInfo_Field.AgentID)
        packUUID(byteBuffer, this.UserInfo_Field.SessionID)
        packVariable(byteBuffer, this.UserInfo_Field.Reason, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TargetBlock_Field.TargetIP = unpackIPAddress(byteBuffer)
        this.TargetBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535
        this.UserInfo_Field.AgentID = unpackUUID(byteBuffer)
        this.UserInfo_Field.SessionID = unpackUUID(byteBuffer)
        this.UserInfo_Field.Reason = unpackVariable(byteBuffer, 2)
    }
}
