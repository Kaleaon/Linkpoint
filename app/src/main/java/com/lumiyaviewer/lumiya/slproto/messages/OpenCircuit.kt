package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer

class OpenCircuit : SLMessage {
    CircuitInfo CircuitInfo_Field = CircuitInfo()

    class CircuitInfo {
        Inet4Address IP
        Int Port
    }

    OpenCircuit() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 10
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOpenCircuit(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) -4)
        packIPAddress(byteBuffer, this.CircuitInfo_Field.IP)
        packShort(byteBuffer, (Short) this.CircuitInfo_Field.Port)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.CircuitInfo_Field.IP = unpackIPAddress(byteBuffer)
        this.CircuitInfo_Field.Port = unpackShort(byteBuffer) & 65535
    }
}
