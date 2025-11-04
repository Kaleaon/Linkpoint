// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class NetTest extends SLMessage
{
    public NetBlock NetBlock_Field;
    
    public NetTest() {
        this.zeroCoded = false;
        this.NetBlock_Field = new NetBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 6;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleNetTest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)70);
        this.packShort(byteBuffer, (short)this.NetBlock_Field.Port);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.NetBlock_Field.Port = (this.unpackShort(byteBuffer) & 0xFFFF);
    }
    
    public static class NetBlock
    {
        public int Port;
    }
}
