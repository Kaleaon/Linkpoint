package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class KickUser : SLMessage() {
    public TargetBlock TargetBlock_Field = TargetBlock()
    public UserInfo UserInfo_Field = UserInfo()

    @JvmStatic
    class TargetBlock {
        public Inet4Address TargetIP
        public Int TargetPort
    }

    @JvmStatic
    class UserInfo {
        public UUID AgentID
        public ByteArray Reason
        public UUID SessionID
    }

    public KickUser() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return this.UserInfo_Field.Reason.length + 34 + 10
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleKickUser(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -93)
        packIPAddress(byteBuffer, this.TargetBlock_Field.TargetIP)
        packShort(byteBuffer, (Short) this.TargetBlock_Field.TargetPort)
        packUUID(byteBuffer, this.UserInfo_Field.AgentID)
        packUUID(byteBuffer, this.UserInfo_Field.SessionID)
        packVariable(byteBuffer, this.UserInfo_Field.Reason, 2)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.TargetBlock_Field.TargetIP = unpackIPAddress(byteBuffer)
        this.TargetBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535
        this.UserInfo_Field.AgentID = unpackUUID(byteBuffer)
        this.UserInfo_Field.SessionID = unpackUUID(byteBuffer)
        this.UserInfo_Field.Reason = unpackVariable(byteBuffer, 2)
    }
}
