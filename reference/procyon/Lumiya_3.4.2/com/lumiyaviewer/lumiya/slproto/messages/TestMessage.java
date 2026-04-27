// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TestMessage extends SLMessage
{
    public NeighborBlock[] NeighborBlock_Fields;
    public TestBlock1 TestBlock1_Field;
    
    public TestMessage() {
        this.NeighborBlock_Fields = new NeighborBlock[4];
        this.zeroCoded = true;
        this.TestBlock1_Field = new TestBlock1();
        for (int i = 0; i < 4; ++i) {
            this.NeighborBlock_Fields[i] = new NeighborBlock();
        }
    }
    
    @Override
    public int CalcPayloadSize() {
        return 56;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTestMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        int i = 0;
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)1);
        this.packInt(byteBuffer, this.TestBlock1_Field.Test1);
        while (i < 4) {
            this.packInt(byteBuffer, this.NeighborBlock_Fields[i].Test0);
            this.packInt(byteBuffer, this.NeighborBlock_Fields[i].Test1);
            this.packInt(byteBuffer, this.NeighborBlock_Fields[i].Test2);
            ++i;
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TestBlock1_Field.Test1 = this.unpackInt(byteBuffer);
        for (int i = 0; i < 4; ++i) {
            this.NeighborBlock_Fields[i].Test0 = this.unpackInt(byteBuffer);
            this.NeighborBlock_Fields[i].Test1 = this.unpackInt(byteBuffer);
            this.NeighborBlock_Fields[i].Test2 = this.unpackInt(byteBuffer);
        }
    }
    
    public static class NeighborBlock
    {
        public int Test0;
        public int Test1;
        public int Test2;
    }
    
    public static class TestBlock1
    {
        public int Test1;
    }
}
