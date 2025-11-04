// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ConfirmXferPacket extends SLMessage
{
    public XferID XferID_Field;
    
    public ConfirmXferPacket() {
        this.zeroCoded = false;
        this.XferID_Field = new XferID();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 13;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleConfirmXferPacket(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)19);
        this.packLong(byteBuffer, this.XferID_Field.ID);
        this.packInt(byteBuffer, this.XferID_Field.Packet);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.XferID_Field.ID = this.unpackLong(byteBuffer);
        this.XferID_Field.Packet = this.unpackInt(byteBuffer);
    }
    
    public static class XferID
    {
        public long ID;
        public int Packet;
    }
}
