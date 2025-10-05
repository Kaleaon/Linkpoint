package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer

class EnableSimulator : SLMessage() {
    val SimulatorInfo_Field = SimulatorInfo()

    class SimulatorInfo {
        var Handle: Long = 0
        var IP: Inet4Address? = null
        var Port: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 18

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleEnableSimulator(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-105).toByte())
        packLong(buffer, SimulatorInfo_Field.Handle)
        packIPAddress(buffer, SimulatorInfo_Field.IP)
        packShort(buffer, SimulatorInfo_Field.Port.toShort())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        SimulatorInfo_Field.Handle = unpackLong(buffer)
        SimulatorInfo_Field.IP = unpackIPAddress(buffer)
        SimulatorInfo_Field.Port = unpackShort(buffer).toInt() and 65535
    }
}