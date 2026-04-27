// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelMediaCommandMessage extends SLMessage
{
    public CommandBlock CommandBlock_Field;
    
    public ParcelMediaCommandMessage() {
        this.zeroCoded = false;
        this.CommandBlock_Field = new CommandBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelMediaCommandMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-93));
        this.packInt(byteBuffer, this.CommandBlock_Field.Flags);
        this.packInt(byteBuffer, this.CommandBlock_Field.Command);
        this.packFloat(byteBuffer, this.CommandBlock_Field.Time);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.CommandBlock_Field.Flags = this.unpackInt(byteBuffer);
        this.CommandBlock_Field.Command = this.unpackInt(byteBuffer);
        this.CommandBlock_Field.Time = this.unpackFloat(byteBuffer);
    }
    
    public static class CommandBlock
    {
        public int Command;
        public int Flags;
        public float Time;
    }
}
