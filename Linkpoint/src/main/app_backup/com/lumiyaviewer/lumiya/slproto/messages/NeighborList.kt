package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        for (Int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i] = NeighborBlock()
        }
    }

    Int CalcPayloadSize() {
        Int i = 1
        for (Int i2 = 0; i2 < 4; i2++) {
            i += this.NeighborBlock_Fields[i2].Name.length + 29 + 1
        }
        return i
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNeighborList(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 3)
        for (Int i = 0; i < 4; i++) {
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].IP)
            packShort(byteBuffer, (Short) this.NeighborBlock_Fields[i].Port)
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].PublicIP)
            packShort(byteBuffer, (Short) this.NeighborBlock_Fields[i].PublicPort)
            packUUID(byteBuffer, this.NeighborBlock_Fields[i].RegionID)
            packVariable(byteBuffer, this.NeighborBlock_Fields[i].Name, 1)
            packByte(byteBuffer, (Byte) this.NeighborBlock_Fields[i].SimAccess)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        for (Int i = 0; i < 4; i++) {
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
