// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EnableSimulator extends SLMessage
{
    public SimulatorInfo SimulatorInfo_Field;
    
    public EnableSimulator() {
        this.zeroCoded = false;
        this.SimulatorInfo_Field = new SimulatorInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 18;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEnableSimulator(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-105));
        this.packLong(byteBuffer, this.SimulatorInfo_Field.Handle);
        this.packIPAddress(byteBuffer, this.SimulatorInfo_Field.IP);
        this.packShort(byteBuffer, (short)this.SimulatorInfo_Field.Port);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SimulatorInfo_Field.Handle = this.unpackLong(byteBuffer);
        this.SimulatorInfo_Field.IP = this.unpackIPAddress(byteBuffer);
        this.SimulatorInfo_Field.Port = (this.unpackShort(byteBuffer) & 0xFFFF);
    }
    
    public static class SimulatorInfo
    {
        public long Handle;
        public Inet4Address IP;
        public int Port;
    }
}
