// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupNoticeAdd extends SLMessage
{
    public AgentData AgentData_Field;
    public MessageBlock MessageBlock_Field;
    
    public GroupNoticeAdd() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.MessageBlock_Field = new MessageBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MessageBlock_Field.FromAgentName.length + 34 + 2 + this.MessageBlock_Field.Message.length + 2 + this.MessageBlock_Field.BinaryBucket.length + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupNoticeAdd(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)61);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.MessageBlock_Field.ToGroupID);
        this.packUUID(byteBuffer, this.MessageBlock_Field.ID);
        this.packByte(byteBuffer, (byte)this.MessageBlock_Field.Dialog);
        this.packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1);
        this.packVariable(byteBuffer, this.MessageBlock_Field.Message, 2);
        this.packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.MessageBlock_Field.ToGroupID = this.unpackUUID(byteBuffer);
        this.MessageBlock_Field.ID = this.unpackUUID(byteBuffer);
        this.MessageBlock_Field.Dialog = (this.unpackByte(byteBuffer) & 0xFF);
        this.MessageBlock_Field.FromAgentName = this.unpackVariable(byteBuffer, 1);
        this.MessageBlock_Field.Message = this.unpackVariable(byteBuffer, 2);
        this.MessageBlock_Field.BinaryBucket = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class MessageBlock
    {
        public byte[] BinaryBucket;
        public int Dialog;
        public byte[] FromAgentName;
        public UUID ID;
        public byte[] Message;
        public UUID ToGroupID;
    }
}
