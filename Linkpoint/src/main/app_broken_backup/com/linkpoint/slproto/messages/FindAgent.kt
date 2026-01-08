package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class FindAgent : SLMessage {
    AgentBlock AgentBlock_Field
    ArrayList<LocationBlock> LocationBlock_Fields = ArrayList<>()

    class AgentBlock {
        UUID Hunter
        UUID Prey
        Inet4Address SpaceIP
    }

    class LocationBlock {
        double GlobalX
        double GlobalY
    }

    FindAgent() {
        this.zeroCoded = false
        this.AgentBlock_Field = AgentBlock()
    }

    fun CalcPayloadSize(): Int {
        return (this.LocationBlock_Fields.size() * 16) + 41
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleFindAgent(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 0)
        packUUID(byteBuffer, this.AgentBlock_Field.Hunter)
        packUUID(byteBuffer, this.AgentBlock_Field.Prey)
        packIPAddress(byteBuffer, this.AgentBlock_Field.SpaceIP)
        byteBuffer.put((this as byte).LocationBlock_Fields.size())
        for (LocationBlock locationBlock : this.LocationBlock_Fields) {
            packDouble(byteBuffer, locationBlock.GlobalX)
            packDouble(byteBuffer, locationBlock.GlobalY)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentBlock_Field.Hunter = unpackUUID(byteBuffer)
        this.AgentBlock_Field.Prey = unpackUUID(byteBuffer)
        this.AgentBlock_Field.SpaceIP = unpackIPAddress(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            LocationBlock locationBlock = LocationBlock()
            locationBlock.GlobalX = unpackDouble(byteBuffer)
            locationBlock.GlobalY = unpackDouble(byteBuffer)
            this.LocationBlock_Fields.add(locationBlock)
        }
    }
}
