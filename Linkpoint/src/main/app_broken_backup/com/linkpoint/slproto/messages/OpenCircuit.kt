package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return 10
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleOpenCircuit(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) -4)
        packIPAddress(byteBuffer, this.CircuitInfo_Field.IP)
        packShort(byteBuffer, (this as Short).CircuitInfo_Field.Port)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.CircuitInfo_Field.IP = unpackIPAddress(byteBuffer)
        this.CircuitInfo_Field.Port = unpackShort(byteBuffer) & 65535
    }
}
