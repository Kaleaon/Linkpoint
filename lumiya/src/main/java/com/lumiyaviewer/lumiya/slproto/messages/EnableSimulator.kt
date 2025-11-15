package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 18
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEnableSimulator(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -105)
        packLong(byteBuffer, this.SimulatorInfo_Field.Handle)
        packIPAddress(byteBuffer, this.SimulatorInfo_Field.IP)
        packShort(byteBuffer, (short) this.SimulatorInfo_Field.Port)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorInfo_Field.Handle = unpackLong(byteBuffer)
        this.SimulatorInfo_Field.IP = unpackIPAddress(byteBuffer)
        this.SimulatorInfo_Field.Port = unpackShort(byteBuffer) & 65535
    }
}
