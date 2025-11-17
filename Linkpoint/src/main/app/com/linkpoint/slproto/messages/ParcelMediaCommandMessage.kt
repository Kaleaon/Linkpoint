package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ParcelMediaCommandMessage : SLMessage {
    CommandBlock CommandBlock_Field = CommandBlock()

    class CommandBlock {
        Int Command
        Int Flags
        Float Time
    }

    ParcelMediaCommandMessage() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 16
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelMediaCommandMessage(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -93)
        packInt(byteBuffer, this.CommandBlock_Field.Flags)
        packInt(byteBuffer, this.CommandBlock_Field.Command)
        packFloat(byteBuffer, this.CommandBlock_Field.Time)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.CommandBlock_Field.Flags = unpackInt(byteBuffer)
        this.CommandBlock_Field.Command = unpackInt(byteBuffer)
        this.CommandBlock_Field.Time = unpackFloat(byteBuffer)
    }
}
