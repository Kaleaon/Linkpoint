// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ViewerEffect extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Effect> Effect_Fields;
    
    public ViewerEffect() {
        this.Effect_Fields = new ArrayList<Effect>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.Effect_Fields.iterator();
        int n = 35;
        while (iterator.hasNext()) {
            n += iterator.next().TypeData.length + 42;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleViewerEffect(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)17);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.Effect_Fields.size());
        for (final Effect effect : this.Effect_Fields) {
            this.packUUID(byteBuffer, effect.ID);
            this.packUUID(byteBuffer, effect.AgentID);
            this.packByte(byteBuffer, (byte)effect.Type);
            this.packFloat(byteBuffer, effect.Duration);
            this.packFixed(byteBuffer, effect.Color, 4);
            this.packVariable(byteBuffer, effect.TypeData, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Effect e = new Effect();
            e.ID = this.unpackUUID(byteBuffer);
            e.AgentID = this.unpackUUID(byteBuffer);
            e.Type = (this.unpackByte(byteBuffer) & 0xFF);
            e.Duration = this.unpackFloat(byteBuffer);
            e.Color = this.unpackFixed(byteBuffer, 4);
            e.TypeData = this.unpackVariable(byteBuffer, 1);
            this.Effect_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Effect
    {
        public UUID AgentID;
        public byte[] Color;
        public float Duration;
        public UUID ID;
        public int Type;
        public byte[] TypeData;
    }
}
