package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class TestMessage extends SLMessage {
    public NeighborBlock[] NeighborBlock_Fields = new NeighborBlock[4];
    public TestBlock1 TestBlock1_Field;

    public static class NeighborBlock {
        public int Test0;
        public int Test1;
        public int Test2;
    }

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

    public int CalcPayloadSize() {
        return 56;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTestMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 1);
        packInt(byteBuffer, this.TestBlock1_Field.Test1);
        for (int i = 0; i < 4; i++) {
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test0);
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test1);
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test2);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TestBlock1_Field.Test1 = unpackInt(byteBuffer);
        for (int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i].Test0 = unpackInt(byteBuffer);
            this.NeighborBlock_Fields[i].Test1 = unpackInt(byteBuffer);
            this.NeighborBlock_Fields[i].Test2 = unpackInt(byteBuffer);
        }
    }
}
