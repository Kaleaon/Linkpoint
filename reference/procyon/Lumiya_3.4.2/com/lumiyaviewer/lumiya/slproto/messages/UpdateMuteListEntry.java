// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateMuteListEntry extends SLMessage
{
    public AgentData AgentData_Field;
    public MuteData MuteData_Field;
    
    public UpdateMuteListEntry() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.MuteData_Field = new MuteData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MuteData_Field.MuteName.length + 17 + 4 + 4 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateMuteListEntry(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)7);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.MuteData_Field.MuteID);
        this.packVariable(byteBuffer, this.MuteData_Field.MuteName, 1);
        this.packInt(byteBuffer, this.MuteData_Field.MuteType);
        this.packInt(byteBuffer, this.MuteData_Field.MuteFlags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.MuteData_Field.MuteID = this.unpackUUID(byteBuffer);
        this.MuteData_Field.MuteName = this.unpackVariable(byteBuffer, 1);
        this.MuteData_Field.MuteType = this.unpackInt(byteBuffer);
        this.MuteData_Field.MuteFlags = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class MuteData
    {
        public int MuteFlags;
        public UUID MuteID;
        public byte[] MuteName;
        public int MuteType;
    }
}
