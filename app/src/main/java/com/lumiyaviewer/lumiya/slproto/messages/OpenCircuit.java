package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

public class OpenCircuit extends SLMessage {
    public CircuitInfo CircuitInfo_Field = new CircuitInfo();

    public static class CircuitInfo {
        public Inet4Address IP;
        public int Port;
    }

    public OpenCircuit() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 10;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOpenCircuit(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) -4);
        packIPAddress(byteBuffer, this.CircuitInfo_Field.IP);
        packShort(byteBuffer, (short) this.CircuitInfo_Field.Port);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.CircuitInfo_Field.IP = unpackIPAddress(byteBuffer);
        this.CircuitInfo_Field.Port = unpackShort(byteBuffer) & 65535;
    }
}
