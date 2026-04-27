// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AbortXfer extends SLMessage
{
    public XferID XferID_Field;
    
    public AbortXfer() {
        this.zeroCoded = false;
        this.XferID_Field = new XferID();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAbortXfer(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-99));
        this.packLong(byteBuffer, this.XferID_Field.ID);
        this.packInt(byteBuffer, this.XferID_Field.Result);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.XferID_Field.ID = this.unpackLong(byteBuffer);
        this.XferID_Field.Result = this.unpackInt(byteBuffer);
    }
    
    public static class XferID
    {
        public long ID;
        public int Result;
    }
}
