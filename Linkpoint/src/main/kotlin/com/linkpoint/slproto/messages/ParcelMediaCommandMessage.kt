package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ParcelMediaCommandMessage : SLMessage() {
    public CommandBlock CommandBlock_Field = CommandBlock()

    @JvmStatic
    class CommandBlock {
        public Int Command
        public Int Flags
        public Float Time
    }

    public ParcelMediaCommandMessage() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 16
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelMediaCommandMessage(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -93)
        packInt(byteBuffer, this.CommandBlock_Field.Flags)
        packInt(byteBuffer, this.CommandBlock_Field.Command)
        packFloat(byteBuffer, this.CommandBlock_Field.Time)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.CommandBlock_Field.Flags = unpackInt(byteBuffer)
        this.CommandBlock_Field.Command = unpackInt(byteBuffer)
        this.CommandBlock_Field.Time = unpackFloat(byteBuffer)
    }
}
