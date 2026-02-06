package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class OpenCircuit extends SLMessage {
    public CircuitInfo CircuitInfo_Field;

    /* loaded from: classes.dex */
    public static class CircuitInfo {
        public Inet4Address IP;
        public int Port;
    }

    public OpenCircuit() {
        this.zeroCoded = false;
        this.CircuitInfo_Field = new CircuitInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 10;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOpenCircuit(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) -4);
        packIPAddress(byteBuffer, this.CircuitInfo_Field.IP);
        packShort(byteBuffer, (short) this.CircuitInfo_Field.Port);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.CircuitInfo_Field.IP = unpackIPAddress(byteBuffer);
        this.CircuitInfo_Field.Port = unpackShort(byteBuffer) & 65535;
    }
}
