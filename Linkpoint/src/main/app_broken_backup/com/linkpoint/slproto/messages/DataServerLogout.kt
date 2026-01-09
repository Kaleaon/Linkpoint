package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class DataServerLogout : SLMessage {
    UserData UserData_Field = UserData()

    class UserData {
        UUID AgentID
        Boolean Disconnect
        UUID SessionID
        Inet4Address ViewerIP
    }

    DataServerLogout() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 41
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDataServerLogout(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -5)
        packUUID(byteBuffer, this.UserData_Field.AgentID)
        packIPAddress(byteBuffer, this.UserData_Field.ViewerIP)
        packBoolean(byteBuffer, this.UserData_Field.Disconnect)
        packUUID(byteBuffer, this.UserData_Field.SessionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.UserData_Field.AgentID = unpackUUID(byteBuffer)
        this.UserData_Field.ViewerIP = unpackIPAddress(byteBuffer)
        this.UserData_Field.Disconnect = unpackBoolean(byteBuffer)
        this.UserData_Field.SessionID = unpackUUID(byteBuffer)
    }
}
