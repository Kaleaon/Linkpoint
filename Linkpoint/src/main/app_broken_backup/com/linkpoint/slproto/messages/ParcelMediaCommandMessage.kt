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

    fun CalcPayloadSize(): Int {
        return 16
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelMediaCommandMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -93)
        packInt(byteBuffer, this.CommandBlock_Field.Flags)
        packInt(byteBuffer, this.CommandBlock_Field.Command)
        packFloat(byteBuffer, this.CommandBlock_Field.Time)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.CommandBlock_Field.Flags = unpackInt(byteBuffer)
        this.CommandBlock_Field.Command = unpackInt(byteBuffer)
        this.CommandBlock_Field.Time = unpackFloat(byteBuffer)
    }
}
