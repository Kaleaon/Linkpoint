package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateUserInfo : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public UserData UserData_Field = UserData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class UserData {
        public ByteArray DirectoryVisibility
        public Boolean IMViaEMail
    }

    public UpdateUserInfo() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleUpdateUserInfo(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -111)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.UserData_Field.IMViaEMail)
        packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.UserData_Field.IMViaEMail = unpackBoolean(byteBuffer)
        this.UserData_Field.DirectoryVisibility = unpackVariable(byteBuffer, 1)
    }
}
