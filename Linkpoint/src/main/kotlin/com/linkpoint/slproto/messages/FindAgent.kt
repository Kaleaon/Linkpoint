package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class FindAgent : SLMessage() {
    public AgentBlock AgentBlock_Field
    public ArrayList<LocationBlock> LocationBlock_Fields = ArrayList<>()

    @JvmStatic
    class AgentBlock {
        public UUID Hunter
        public UUID Prey
        public Inet4Address SpaceIP
    }

    @JvmStatic
    class LocationBlock {
        public Double GlobalX
        public Double GlobalY
    }

    public FindAgent() {
        this.zeroCoded = false
        this.AgentBlock_Field = AgentBlock()
    }

    public fun CalcPayloadSize(): Int {
        return (this.LocationBlock_Fields.size() * 16) + 41
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleFindAgent(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 0)
        packUUID(byteBuffer, this.AgentBlock_Field.Hunter)
        packUUID(byteBuffer, this.AgentBlock_Field.Prey)
        packIPAddress(byteBuffer, this.AgentBlock_Field.SpaceIP)
        byteBuffer.put((Byte) this.LocationBlock_Fields.size())
        for (LocationBlock locationBlock : this.LocationBlock_Fields) {
            packDouble(byteBuffer, locationBlock.GlobalX)
            packDouble(byteBuffer, locationBlock.GlobalY)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentBlock_Field.Hunter = unpackUUID(byteBuffer)
        this.AgentBlock_Field.Prey = unpackUUID(byteBuffer)
        this.AgentBlock_Field.SpaceIP = unpackIPAddress(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val locationBlock: LocationBlock = LocationBlock()
            locationBlock.GlobalX = unpackDouble(byteBuffer)
            locationBlock.GlobalY = unpackDouble(byteBuffer)
            this.LocationBlock_Fields.add(locationBlock)
        }
    }
}
