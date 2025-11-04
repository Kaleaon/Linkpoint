// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ChatPass extends SLMessage
{
    public ChatData ChatData_Field;
    
    public ChatPass() {
        this.zeroCoded = true;
        this.ChatData_Field = new ChatData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ChatData_Field.Name.length + 49 + 1 + 1 + 4 + 1 + 2 + this.ChatData_Field.Message.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleChatPass(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-17));
        this.packInt(byteBuffer, this.ChatData_Field.Channel);
        this.packLLVector3(byteBuffer, this.ChatData_Field.Position);
        this.packUUID(byteBuffer, this.ChatData_Field.ID);
        this.packUUID(byteBuffer, this.ChatData_Field.OwnerID);
        this.packVariable(byteBuffer, this.ChatData_Field.Name, 1);
        this.packByte(byteBuffer, (byte)this.ChatData_Field.SourceType);
        this.packByte(byteBuffer, (byte)this.ChatData_Field.Type);
        this.packFloat(byteBuffer, this.ChatData_Field.Radius);
        this.packByte(byteBuffer, (byte)this.ChatData_Field.SimAccess);
        this.packVariable(byteBuffer, this.ChatData_Field.Message, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ChatData_Field.Channel = this.unpackInt(byteBuffer);
        this.ChatData_Field.Position = this.unpackLLVector3(byteBuffer);
        this.ChatData_Field.ID = this.unpackUUID(byteBuffer);
        this.ChatData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.ChatData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.ChatData_Field.SourceType = (this.unpackByte(byteBuffer) & 0xFF);
        this.ChatData_Field.Type = (this.unpackByte(byteBuffer) & 0xFF);
        this.ChatData_Field.Radius = this.unpackFloat(byteBuffer);
        this.ChatData_Field.SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
        this.ChatData_Field.Message = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class ChatData
    {
        public int Channel;
        public UUID ID;
        public byte[] Message;
        public byte[] Name;
        public UUID OwnerID;
        public LLVector3 Position;
        public float Radius;
        public int SimAccess;
        public int SourceType;
        public int Type;
    }
}
