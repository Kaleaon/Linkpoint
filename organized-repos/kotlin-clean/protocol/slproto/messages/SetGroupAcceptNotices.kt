package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetGroupAcceptNotices : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()
    public NewData NewData_Field = NewData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public Boolean AcceptNotices
        public UUID GroupID
    }

    @JvmStatic
    class NewData {
        public Boolean ListInProfile
    }

    public SetGroupAcceptNotices() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 54
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetGroupAcceptNotices(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 114)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.GroupID)
        packBoolean(byteBuffer, this.Data_Field.AcceptNotices)
        packBoolean(byteBuffer, this.NewData_Field.ListInProfile)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.GroupID = unpackUUID(byteBuffer)
        this.Data_Field.AcceptNotices = unpackBoolean(byteBuffer)
        this.NewData_Field.ListInProfile = unpackBoolean(byteBuffer)
    }
}
