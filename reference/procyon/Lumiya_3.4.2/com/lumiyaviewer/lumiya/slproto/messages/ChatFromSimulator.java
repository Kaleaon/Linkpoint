// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ChatFromSimulator extends SLMessage
{
    public ChatData ChatData_Field;
    
    public ChatFromSimulator() {
        this.zeroCoded = false;
        this.ChatData_Field = new ChatData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ChatData_Field.FromName.length + 1 + 16 + 16 + 1 + 1 + 1 + 12 + 2 + this.ChatData_Field.Message.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleChatFromSimulator(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-117));
        this.packVariable(byteBuffer, this.ChatData_Field.FromName, 1);
        this.packUUID(byteBuffer, this.ChatData_Field.SourceID);
        this.packUUID(byteBuffer, this.ChatData_Field.OwnerID);
        this.packByte(byteBuffer, (byte)this.ChatData_Field.SourceType);
        this.packByte(byteBuffer, (byte)this.ChatData_Field.ChatType);
        this.packByte(byteBuffer, (byte)this.ChatData_Field.Audible);
        this.packLLVector3(byteBuffer, this.ChatData_Field.Position);
        this.packVariable(byteBuffer, this.ChatData_Field.Message, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ChatData_Field.FromName = this.unpackVariable(byteBuffer, 1);
        this.ChatData_Field.SourceID = this.unpackUUID(byteBuffer);
        this.ChatData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.ChatData_Field.SourceType = (this.unpackByte(byteBuffer) & 0xFF);
        this.ChatData_Field.ChatType = (this.unpackByte(byteBuffer) & 0xFF);
        this.ChatData_Field.Audible = (this.unpackByte(byteBuffer) & 0xFF);
        this.ChatData_Field.Position = this.unpackLLVector3(byteBuffer);
        this.ChatData_Field.Message = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class ChatData
    {
        public int Audible;
        public int ChatType;
        public byte[] FromName;
        public byte[] Message;
        public UUID OwnerID;
        public LLVector3 Position;
        public UUID SourceID;
        public int SourceType;
    }
}
