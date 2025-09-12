package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class ParcelMediaCommandMessage extends SLMessage {
    public CommandBlock CommandBlock_Field = new CommandBlock();

    public static class CommandBlock {
        public int Command;
        public int Flags;
        public float Time;
    }

    public ParcelMediaCommandMessage() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 16;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelMediaCommandMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -93);
        packInt(byteBuffer, this.CommandBlock_Field.Flags);
        packInt(byteBuffer, this.CommandBlock_Field.Command);
        packFloat(byteBuffer, this.CommandBlock_Field.Time);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.CommandBlock_Field.Flags = unpackInt(byteBuffer);
        this.CommandBlock_Field.Command = unpackInt(byteBuffer);
        this.CommandBlock_Field.Time = unpackFloat(byteBuffer);
    }
}
