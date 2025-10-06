package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer

class OpenCircuit : SLMessage() {
    public CircuitInfo CircuitInfo_Field = CircuitInfo()

    @JvmStatic
    class CircuitInfo {
        public Inet4Address IP
        public Int Port
    }

    public OpenCircuit() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 10
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOpenCircuit(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) -4)
        packIPAddress(byteBuffer, this.CircuitInfo_Field.IP)
        packShort(byteBuffer, (Short) this.CircuitInfo_Field.Port)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.CircuitInfo_Field.IP = unpackIPAddress(byteBuffer)
        this.CircuitInfo_Field.Port = unpackShort(byteBuffer) & 65535
    }
}
