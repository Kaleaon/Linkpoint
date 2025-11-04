// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EventInfoReply extends SLMessage
{
    public AgentData AgentData_Field;
    public EventData EventData_Field;
    
    public EventInfoReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.EventData_Field = new EventData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.EventData_Field.Creator.length + 5 + 1 + this.EventData_Field.Name.length + 1 + this.EventData_Field.Category.length + 2 + this.EventData_Field.Desc.length + 1 + this.EventData_Field.Date.length + 4 + 4 + 4 + 4 + 1 + this.EventData_Field.SimName.length + 24 + 4 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEventInfoReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-76));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packInt(byteBuffer, this.EventData_Field.EventID);
        this.packVariable(byteBuffer, this.EventData_Field.Creator, 1);
        this.packVariable(byteBuffer, this.EventData_Field.Name, 1);
        this.packVariable(byteBuffer, this.EventData_Field.Category, 1);
        this.packVariable(byteBuffer, this.EventData_Field.Desc, 2);
        this.packVariable(byteBuffer, this.EventData_Field.Date, 1);
        this.packInt(byteBuffer, this.EventData_Field.DateUTC);
        this.packInt(byteBuffer, this.EventData_Field.Duration);
        this.packInt(byteBuffer, this.EventData_Field.Cover);
        this.packInt(byteBuffer, this.EventData_Field.Amount);
        this.packVariable(byteBuffer, this.EventData_Field.SimName, 1);
        this.packLLVector3d(byteBuffer, this.EventData_Field.GlobalPos);
        this.packInt(byteBuffer, this.EventData_Field.EventFlags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.EventData_Field.EventID = this.unpackInt(byteBuffer);
        this.EventData_Field.Creator = this.unpackVariable(byteBuffer, 1);
        this.EventData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.EventData_Field.Category = this.unpackVariable(byteBuffer, 1);
        this.EventData_Field.Desc = this.unpackVariable(byteBuffer, 2);
        this.EventData_Field.Date = this.unpackVariable(byteBuffer, 1);
        this.EventData_Field.DateUTC = this.unpackInt(byteBuffer);
        this.EventData_Field.Duration = this.unpackInt(byteBuffer);
        this.EventData_Field.Cover = this.unpackInt(byteBuffer);
        this.EventData_Field.Amount = this.unpackInt(byteBuffer);
        this.EventData_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.EventData_Field.GlobalPos = this.unpackLLVector3d(byteBuffer);
        this.EventData_Field.EventFlags = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class EventData
    {
        public int Amount;
        public byte[] Category;
        public int Cover;
        public byte[] Creator;
        public byte[] Date;
        public int DateUTC;
        public byte[] Desc;
        public int Duration;
        public int EventFlags;
        public int EventID;
        public LLVector3d GlobalPos;
        public byte[] Name;
        public byte[] SimName;
    }
}
