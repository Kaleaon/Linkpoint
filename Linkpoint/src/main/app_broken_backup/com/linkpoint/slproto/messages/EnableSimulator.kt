package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer

class EnableSimulator : SLMessage {
    SimulatorInfo SimulatorInfo_Field = SimulatorInfo()

    class SimulatorInfo {
        Long Handle
        Inet4Address IP
        Int Port
    }

    EnableSimulator() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 18
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleEnableSimulator(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -105)
        packLong(byteBuffer, this.SimulatorInfo_Field.Handle)
        packIPAddress(byteBuffer, this.SimulatorInfo_Field.IP)
        packShort(byteBuffer, (this as short).SimulatorInfo_Field.Port)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.SimulatorInfo_Field.Handle = unpackLong(byteBuffer)
        this.SimulatorInfo_Field.IP = unpackIPAddress(byteBuffer)
        this.SimulatorInfo_Field.Port = unpackShort(byteBuffer) & 65535
    }
}
