package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class EnableSimulator extends SLMessage {
    public SimulatorInfo SimulatorInfo_Field;

    /* loaded from: classes.dex */
    public static class SimulatorInfo {
        public long Handle;
        public Inet4Address IP;
        public int Port;
    }

    public EnableSimulator() {
        this.zeroCoded = false;
        this.SimulatorInfo_Field = new SimulatorInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 18;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEnableSimulator(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -105);
        packLong(byteBuffer, this.SimulatorInfo_Field.Handle);
        packIPAddress(byteBuffer, this.SimulatorInfo_Field.IP);
        packShort(byteBuffer, (short) this.SimulatorInfo_Field.Port);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorInfo_Field.Handle = unpackLong(byteBuffer);
        this.SimulatorInfo_Field.IP = unpackIPAddress(byteBuffer);
        this.SimulatorInfo_Field.Port = unpackShort(byteBuffer) & 65535;
    }
}
