// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class OpenCircuit extends SLMessage
{
    public CircuitInfo CircuitInfo_Field;
    
    public OpenCircuit() {
        this.zeroCoded = false;
        this.CircuitInfo_Field = new CircuitInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 10;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleOpenCircuit(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)(-4));
        this.packIPAddress(byteBuffer, this.CircuitInfo_Field.IP);
        this.packShort(byteBuffer, (short)this.CircuitInfo_Field.Port);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.CircuitInfo_Field.IP = this.unpackIPAddress(byteBuffer);
        this.CircuitInfo_Field.Port = (this.unpackShort(byteBuffer) & 0xFFFF);
    }
    
    public static class CircuitInfo
    {
        public Inet4Address IP;
        public int Port;
    }
}
