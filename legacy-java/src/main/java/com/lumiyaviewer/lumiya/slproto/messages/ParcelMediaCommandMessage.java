package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ParcelMediaCommandMessage extends SLMessage {
    public CommandBlock CommandBlock_Field;

    /* loaded from: classes.dex */
    public static class CommandBlock {
        public int Command;
        public int Flags;
        public float Time;
    }

    public ParcelMediaCommandMessage() {
        this.zeroCoded = false;
        this.CommandBlock_Field = new CommandBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 16;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelMediaCommandMessage(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -93);
        packInt(byteBuffer, this.CommandBlock_Field.Flags);
        packInt(byteBuffer, this.CommandBlock_Field.Command);
        packFloat(byteBuffer, this.CommandBlock_Field.Time);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.CommandBlock_Field.Flags = unpackInt(byteBuffer);
        this.CommandBlock_Field.Command = unpackInt(byteBuffer);
        this.CommandBlock_Field.Time = unpackFloat(byteBuffer);
    }
}
