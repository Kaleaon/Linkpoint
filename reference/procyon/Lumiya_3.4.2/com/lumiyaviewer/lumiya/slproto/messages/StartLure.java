// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class StartLure extends SLMessage
{
    public AgentData AgentData_Field;
    public Info Info_Field;
    public ArrayList<TargetData> TargetData_Fields;
    
    public StartLure() {
        this.TargetData_Fields = new ArrayList<TargetData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Info_Field.Message.length + 2 + 36 + 1 + this.TargetData_Fields.size() * 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleStartLure(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)70);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packByte(byteBuffer, (byte)this.Info_Field.LureType);
        this.packVariable(byteBuffer, this.Info_Field.Message, 1);
        byteBuffer.put((byte)this.TargetData_Fields.size());
        final Iterator<Object> iterator = this.TargetData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().TargetID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Info_Field.LureType = (this.unpackByte(byteBuffer) & 0xFF);
        this.Info_Field.Message = this.unpackVariable(byteBuffer, 1);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final TargetData e = new TargetData();
            e.TargetID = this.unpackUUID(byteBuffer);
            this.TargetData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Info
    {
        public int LureType;
        public byte[] Message;
    }
    
    public static class TargetData
    {
        public UUID TargetID;
    }
}
