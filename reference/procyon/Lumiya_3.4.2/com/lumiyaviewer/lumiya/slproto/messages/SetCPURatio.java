// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetCPURatio extends SLMessage
{
    public Data Data_Field;
    
    public SetCPURatio() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetCPURatio(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)71);
        this.packByte(byteBuffer, (byte)this.Data_Field.Ratio);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.Ratio = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class Data
    {
        public int Ratio;
    }
}
