package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class TestMessage : SLMessage {
    NeighborBlock[] NeighborBlock_Fields = NeighborBlock[4]
    TestBlock1 TestBlock1_Field

    class NeighborBlock {
        Int Test0
        Int Test1
        Int Test2
    }

    class TestBlock1 {
        Int Test1
    }

    TestMessage() {
        this.zeroCoded = true
        this.TestBlock1_Field = TestBlock1()
        for (Int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i] = NeighborBlock()
        }
    }

    Int CalcPayloadSize() {
        return 56
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTestMessage(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TestBlock1_Field.Test1 = unpackInt(byteBuffer)
        for (Int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i].Test0 = unpackInt(byteBuffer)
            this.NeighborBlock_Fields[i].Test1 = unpackInt(byteBuffer)
            this.NeighborBlock_Fields[i].Test2 = unpackInt(byteBuffer)
        }
    }
}
