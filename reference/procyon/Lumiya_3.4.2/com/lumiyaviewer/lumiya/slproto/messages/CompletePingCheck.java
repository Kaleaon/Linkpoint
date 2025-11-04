// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CompletePingCheck extends SLMessage
{
    public PingID PingID_Field;
    
    public CompletePingCheck() {
        this.zeroCoded = false;
        this.PingID_Field = new PingID();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 2;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCompletePingCheck(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)2);
        this.packByte(byteBuffer, (byte)this.PingID_Field.PingID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class PingID
    {
        public int PingID;
    }
}
