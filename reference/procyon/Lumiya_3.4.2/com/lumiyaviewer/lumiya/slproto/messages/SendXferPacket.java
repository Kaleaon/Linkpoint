// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SendXferPacket extends SLMessage
{
    public DataPacket DataPacket_Field;
    public XferID XferID_Field;
    
    public SendXferPacket() {
        this.zeroCoded = false;
        this.XferID_Field = new XferID();
        this.DataPacket_Field = new DataPacket();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataPacket_Field.Data.length + 2 + 13;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSendXferPacket(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)18);
        this.packLong(byteBuffer, this.XferID_Field.ID);
        this.packInt(byteBuffer, this.XferID_Field.Packet);
        this.packVariable(byteBuffer, this.DataPacket_Field.Data, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.XferID_Field.ID = this.unpackLong(byteBuffer);
        this.XferID_Field.Packet = this.unpackInt(byteBuffer);
        this.DataPacket_Field.Data = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class DataPacket
    {
        public byte[] Data;
    }
    
    public static class XferID
    {
        public long ID;
        public int Packet;
    }
}
