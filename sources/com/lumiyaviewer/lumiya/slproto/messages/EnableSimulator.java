package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

public class EnableSimulator extends SLMessage {
    public SimulatorInfo SimulatorInfo_Field = new SimulatorInfo();

    public static class SimulatorInfo {
        public long Handle;
        public Inet4Address IP;
        public int Port;
    }

    public EnableSimulator() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 18;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEnableSimulator(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -105);
        packLong(byteBuffer, this.SimulatorInfo_Field.Handle);
        packIPAddress(byteBuffer, this.SimulatorInfo_Field.IP);
        packShort(byteBuffer, (short) this.SimulatorInfo_Field.Port);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorInfo_Field.Handle = unpackLong(byteBuffer);
        this.SimulatorInfo_Field.IP = unpackIPAddress(byteBuffer);
        this.SimulatorInfo_Field.Port = unpackShort(byteBuffer) & 65535;
    }
}
