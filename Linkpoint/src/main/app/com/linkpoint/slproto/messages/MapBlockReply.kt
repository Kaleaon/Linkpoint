package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class MapBlockReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<Data> Data_Fields = ArrayList<>()
    ArrayList<Size> Size_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Int Flags
    }

    class Data {
        Int Access
        Int Agents
        UUID MapImageID
        ByteArray Name
        Int RegionFlags
        Int WaterHeight
        Int X
        Int Y
    }

    class Size {
        Int SizeX
        Int SizeY
    }

    MapBlockReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 25
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.Size_Fields.size() * 4)
            }
            i = ((it as Data).next()).Name.size + 5 + 1 + 4 + 1 + 1 + 16 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleMapBlockReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -103)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        byteBuffer.put((this as Byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packShort(byteBuffer, (data as Short).X)
            packShort(byteBuffer, (data as Short).Y)
            packVariable(byteBuffer, data.Name, 1)
            packByte(byteBuffer, (data as Byte).Access)
            packInt(byteBuffer, data.RegionFlags)
            packByte(byteBuffer, (data as Byte).WaterHeight)
            packByte(byteBuffer, (data as Byte).Agents)
            packUUID(byteBuffer, data.MapImageID)
        }
        byteBuffer.put((this as Byte).Size_Fields.size())
        for (Size size : this.Size_Fields) {
            packShort(byteBuffer, (size as Short).SizeX)
            packShort(byteBuffer, (size as Short).SizeY)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.X = unpackShort(byteBuffer) & 65535
            data.Y = unpackShort(byteBuffer) & 65535
            data.Name = unpackVariable(byteBuffer, 1)
            data.Access = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            data.RegionFlags = unpackInt(byteBuffer)
            data.WaterHeight = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            data.Agents = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            data.MapImageID = unpackUUID(byteBuffer)
            this.Data_Fields.add(data)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            Size size = Size()
            size.SizeX = unpackShort(byteBuffer) & 65535
            size.SizeY = unpackShort(byteBuffer) & 65535
            this.Size_Fields.add(size)
        }
    }
}
