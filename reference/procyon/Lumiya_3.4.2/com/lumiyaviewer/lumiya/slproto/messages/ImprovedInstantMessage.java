// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ImprovedInstantMessage extends SLMessage
{
    public AgentData AgentData_Field;
    public MessageBlock MessageBlock_Field;
    
    public ImprovedInstantMessage() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MessageBlock_Field = new MessageBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MessageBlock_Field.FromAgentName.length + 72 + 2 + this.MessageBlock_Field.Message.length + 2 + this.MessageBlock_Field.BinaryBucket.length + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleImprovedInstantMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-2));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packBoolean(byteBuffer, this.MessageBlock_Field.FromGroup);
        this.packUUID(byteBuffer, this.MessageBlock_Field.ToAgentID);
        this.packInt(byteBuffer, this.MessageBlock_Field.ParentEstateID);
        this.packUUID(byteBuffer, this.MessageBlock_Field.RegionID);
        this.packLLVector3(byteBuffer, this.MessageBlock_Field.Position);
        this.packByte(byteBuffer, (byte)this.MessageBlock_Field.Offline);
        this.packByte(byteBuffer, (byte)this.MessageBlock_Field.Dialog);
        this.packUUID(byteBuffer, this.MessageBlock_Field.ID);
        this.packInt(byteBuffer, this.MessageBlock_Field.Timestamp);
        this.packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1);
        this.packVariable(byteBuffer, this.MessageBlock_Field.Message, 2);
        this.packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.MessageBlock_Field.FromGroup = this.unpackBoolean(byteBuffer);
        this.MessageBlock_Field.ToAgentID = this.unpackUUID(byteBuffer);
        this.MessageBlock_Field.ParentEstateID = this.unpackInt(byteBuffer);
        this.MessageBlock_Field.RegionID = this.unpackUUID(byteBuffer);
        this.MessageBlock_Field.Position = this.unpackLLVector3(byteBuffer);
        this.MessageBlock_Field.Offline = (this.unpackByte(byteBuffer) & 0xFF);
        this.MessageBlock_Field.Dialog = (this.unpackByte(byteBuffer) & 0xFF);
        this.MessageBlock_Field.ID = this.unpackUUID(byteBuffer);
        this.MessageBlock_Field.Timestamp = this.unpackInt(byteBuffer);
        this.MessageBlock_Field.FromAgentName = this.unpackVariable(byteBuffer, 1);
        this.MessageBlock_Field.Message = this.unpackVariable(byteBuffer, 2);
        this.MessageBlock_Field.BinaryBucket = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class MessageBlock
    {
        public byte[] BinaryBucket;
        public int Dialog;
        public byte[] FromAgentName;
        public boolean FromGroup;
        public UUID ID;
        public byte[] Message;
        public int Offline;
        public int ParentEstateID;
        public LLVector3 Position;
        public UUID RegionID;
        public int Timestamp;
        public UUID ToAgentID;
    }
}
