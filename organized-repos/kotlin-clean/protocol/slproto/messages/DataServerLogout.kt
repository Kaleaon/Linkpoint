package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class DataServerLogout : SLMessage() {
    public UserData UserData_Field = UserData()

    @JvmStatic
    class UserData {
        public UUID AgentID
        public Boolean Disconnect
        public UUID SessionID
        public Inet4Address ViewerIP
    }

    public DataServerLogout() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 41
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataServerLogout(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -5)
        packUUID(byteBuffer, this.UserData_Field.AgentID)
        packIPAddress(byteBuffer, this.UserData_Field.ViewerIP)
        packBoolean(byteBuffer, this.UserData_Field.Disconnect)
        packUUID(byteBuffer, this.UserData_Field.SessionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.UserData_Field.AgentID = unpackUUID(byteBuffer)
        this.UserData_Field.ViewerIP = unpackIPAddress(byteBuffer)
        this.UserData_Field.Disconnect = unpackBoolean(byteBuffer)
        this.UserData_Field.SessionID = unpackUUID(byteBuffer)
    }
}
