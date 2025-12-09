package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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
        for (i in 0 until 4) {
            this.NeighborBlock_Fields[i] = NeighborBlock()
        }
    }

    fun CalcPayloadSize(): Int {
        return 56
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTestMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 1)
        packInt(byteBuffer, this.TestBlock1_Field.Test1)
        for (i in 0 until 4) {
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test0)
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test1)
            packInt(byteBuffer, this.NeighborBlock_Fields[i].Test2)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TestBlock1_Field.Test1 = unpackInt(byteBuffer)
        for (i in 0 until 4) {
            this.NeighborBlock_Fields[i].Test0 = unpackInt(byteBuffer)
            this.NeighborBlock_Fields[i].Test1 = unpackInt(byteBuffer)
            this.NeighborBlock_Fields[i].Test2 = unpackInt(byteBuffer)
        }
    }
}
