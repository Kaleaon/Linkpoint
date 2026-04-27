package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class TestMessage extends SLMessage {
    public NeighborBlock[] NeighborBlock_Fields = new NeighborBlock[4];
    public TestBlock1 TestBlock1_Field;

    /* loaded from: classes.dex */
    public static class NeighborBlock {
        public int Test0;
        public int Test1;
        public int Test2;
    }

    /* loaded from: classes.dex */
    public static class TestBlock1 {
        public int Test1;
    }

    public TestMessage() {
        this.zeroCoded = true;
        this.TestBlock1_Field = new TestBlock1();
        for (int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i] = new NeighborBlock();
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 56;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTestMessage(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 1);
        packInt(byteBuffer, this.TestBlock1_Field.Test1);
        for (int i = 0; i < 4; i++) {
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test0);
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test1);
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test2);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TestBlock1_Field.Test1 = unpackInt(byteBuffer);
        for (int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i].Test0 = unpackInt(byteBuffer);
            this.NeighborBlock_Fields[i].Test1 = unpackInt(byteBuffer);
            this.NeighborBlock_Fields[i].Test2 = unpackInt(byteBuffer);
        }
    }
}
