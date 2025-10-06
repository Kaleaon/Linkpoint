package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class SimCrashed : SLMessage() {
    public Data Data_Field
    public ArrayList<Users> Users_Fields = ArrayList<>()

    @JvmStatic
    class Data {
        public Int RegionX
        public Int RegionY
    }

    @JvmStatic
    class Users {
        public UUID AgentID
    }

    public SimCrashed() {
        this.zeroCoded = false
        this.Data_Field = Data()
    }

    public Int CalcPayloadSize() {
        return (this.Users_Fields.size() * 16) + 13
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimCrashed(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 72)
        packInt(byteBuffer, this.Data_Field.RegionX)
        packInt(byteBuffer, this.Data_Field.RegionY)
        byteBuffer.put((Byte) this.Users_Fields.size())
        for (Users users : this.Users_Fields) {
            packUUID(byteBuffer, users.AgentID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.RegionX = unpackInt(byteBuffer)
        this.Data_Field.RegionY = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Users users = Users()
            users.AgentID = unpackUUID(byteBuffer)
            this.Users_Fields.add(users)
        }
    }
}
