package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class NeighborList : SLMessage {
    NeighborBlock[] NeighborBlock_Fields = NeighborBlock[4]

    class NeighborBlock {
        Inet4Address IP
        ByteArray Name
        Int Port
        Inet4Address PublicIP
        Int PublicPort
        UUID RegionID
        Int SimAccess
    }

    NeighborList() {
        this.zeroCoded = false
        for (i in 0 until 4) {
            this.NeighborBlock_Fields[i] = NeighborBlock()
        }
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 1
        for (i2 in 0 until 4) {
            i += this.NeighborBlock_Fields[i2].Name.size + 29 + 1
        }
        return i
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleNeighborList(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((Byte) 3)
        for (i in 0 until 4) {
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].IP)
            packShort(byteBuffer, (this as Short).NeighborBlock_Fields[i].Port)
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].PublicIP)
            packShort(byteBuffer, (this as Short).NeighborBlock_Fields[i].PublicPort)
            packUUID(byteBuffer, this.NeighborBlock_Fields[i].RegionID)
            packVariable(byteBuffer, this.NeighborBlock_Fields[i].Name, 1)
            packByte(byteBuffer, (this as Byte).NeighborBlock_Fields[i].SimAccess)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        for (i in 0 until 4) {
            this.NeighborBlock_Fields[i].IP = unpackIPAddress(byteBuffer)
            this.NeighborBlock_Fields[i].Port = unpackShort(byteBuffer) & 65535
            this.NeighborBlock_Fields[i].PublicIP = unpackIPAddress(byteBuffer)
            this.NeighborBlock_Fields[i].PublicPort = unpackShort(byteBuffer) & 65535
            this.NeighborBlock_Fields[i].RegionID = unpackUUID(byteBuffer)
            this.NeighborBlock_Fields[i].Name = unpackVariable(byteBuffer, 1)
            this.NeighborBlock_Fields[i].SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        }
    }
}
