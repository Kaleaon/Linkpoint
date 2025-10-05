package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TestMessage : SLMessage() {
    public NeighborBlock[] NeighborBlock_Fields = NeighborBlock[4]
    public TestBlock1 TestBlock1_Field

    @JvmStatic
    class NeighborBlock {
        public Int Test0
        public Int Test1
        public Int Test2
    }

    @JvmStatic
    class TestBlock1 {
        public Int Test1
    }

    public TestMessage() {
        this.zeroCoded = true
        this.TestBlock1_Field = TestBlock1()
        for (Int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i] = NeighborBlock()
        }
    }

    public Int CalcPayloadSize() {
        return 56
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTestMessage(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 1)
        packInt(byteBuffer, this.TestBlock1_Field.Test1)
        for (Int i = 0; i < 4; i++) {
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test0)
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test1)
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test2)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TestBlock1_Field.Test1 = unpackInt(byteBuffer)
        for (Int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i].Test0 = unpackInt(byteBuffer)
            this.NeighborBlock_Fields[i].Test1 = unpackInt(byteBuffer)
            this.NeighborBlock_Fields[i].Test2 = unpackInt(byteBuffer)
        }
    }
}
