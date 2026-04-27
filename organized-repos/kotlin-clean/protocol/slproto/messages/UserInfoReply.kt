package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UserInfoReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public UserData UserData_Field = UserData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class UserData {
        public ByteArray DirectoryVisibility
        public ByteArray EMail
        public Boolean IMViaEMail
    }

    public UserInfoReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 2 + this.UserData_Field.EMail.length + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserInfoReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -112)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packBoolean(byteBuffer, this.UserData_Field.IMViaEMail)
        packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1)
        packVariable(byteBuffer, this.UserData_Field.EMail, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.UserData_Field.IMViaEMail = unpackBoolean(byteBuffer)
        this.UserData_Field.DirectoryVisibility = unpackVariable(byteBuffer, 1)
        this.UserData_Field.EMail = unpackVariable(byteBuffer, 2)
    }
}
