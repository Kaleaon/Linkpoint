// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class StartPingCheck extends SLMessage
{
    public PingID PingID_Field;
    
    public StartPingCheck() {
        this.zeroCoded = false;
        this.PingID_Field = new PingID();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 6;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleStartPingCheck(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)1);
        this.packByte(byteBuffer, (byte)this.PingID_Field.PingID);
        this.packInt(byteBuffer, this.PingID_Field.OldestUnacked);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = (this.unpackByte(byteBuffer) & 0xFF);
        this.PingID_Field.OldestUnacked = this.unpackInt(byteBuffer);
    }
    
    public static class PingID
    {
        public int OldestUnacked;
        public int PingID;
    }
}
