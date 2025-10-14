package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UserInfoReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    UserData UserData_Field = UserData()

    class AgentData {
        UUID AgentID
    }

    class UserData {
        Byte[] DirectoryVisibility
        Byte[] EMail
        Boolean IMViaEMail
    }

    UserInfoReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 2 + this.UserData_Field.EMail.length + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserInfoReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -112)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packBoolean(byteBuffer, this.UserData_Field.IMViaEMail)
        packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1)
        packVariable(byteBuffer, this.UserData_Field.EMail, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.UserData_Field.IMViaEMail = unpackBoolean(byteBuffer)
        this.UserData_Field.DirectoryVisibility = unpackVariable(byteBuffer, 1)
        this.UserData_Field.EMail = unpackVariable(byteBuffer, 2)
    }
}
