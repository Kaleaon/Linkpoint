package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class SimCrashed : SLMessage {
    Data Data_Field
    ArrayList<Users> Users_Fields = ArrayList<>()

    class Data {
        Int RegionX
        Int RegionY
    }

    class Users {
        UUID AgentID
    }

    SimCrashed() {
        this.zeroCoded = false
        this.Data_Field = Data()
    }

    Int CalcPayloadSize() {
        return (this.Users_Fields.size() * 16) + 13
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimCrashed(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
