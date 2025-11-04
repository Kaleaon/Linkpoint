// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CreateGroupReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ReplyData ReplyData_Field;
    
    public CreateGroupReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ReplyData_Field = new ReplyData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ReplyData_Field.Message.length + 18 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCreateGroupReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)84);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.ReplyData_Field.GroupID);
        this.packBoolean(byteBuffer, this.ReplyData_Field.Success);
        this.packVariable(byteBuffer, this.ReplyData_Field.Message, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.ReplyData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ReplyData_Field.Success = this.unpackBoolean(byteBuffer);
        this.ReplyData_Field.Message = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class ReplyData
    {
        public UUID GroupID;
        public byte[] Message;
        public boolean Success;
    }
}
