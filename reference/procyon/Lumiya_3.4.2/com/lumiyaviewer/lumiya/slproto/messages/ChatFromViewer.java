// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ChatFromViewer extends SLMessage
{
    public AgentData AgentData_Field;
    public ChatData ChatData_Field;
    
    public ChatFromViewer() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ChatData_Field = new ChatData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ChatData_Field.Message.length + 2 + 1 + 4 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleChatFromViewer(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)80);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packVariable(byteBuffer, this.ChatData_Field.Message, 2);
        this.packByte(byteBuffer, (byte)this.ChatData_Field.Type);
        this.packInt(byteBuffer, this.ChatData_Field.Channel);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ChatData_Field.Message = this.unpackVariable(byteBuffer, 2);
        this.ChatData_Field.Type = (this.unpackByte(byteBuffer) & 0xFF);
        this.ChatData_Field.Channel = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ChatData
    {
        public int Channel;
        public byte[] Message;
        public int Type;
    }
}
