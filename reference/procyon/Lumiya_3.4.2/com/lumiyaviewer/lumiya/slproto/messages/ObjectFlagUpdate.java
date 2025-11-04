// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectFlagUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<ExtraPhysics> ExtraPhysics_Fields;
    
    public ObjectFlagUpdate() {
        this.ExtraPhysics_Fields = new ArrayList<ExtraPhysics>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ExtraPhysics_Fields.size() * 17 + 45;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectFlagUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)94);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.ObjectLocalID);
        this.packBoolean(byteBuffer, this.AgentData_Field.UsePhysics);
        this.packBoolean(byteBuffer, this.AgentData_Field.IsTemporary);
        this.packBoolean(byteBuffer, this.AgentData_Field.IsPhantom);
        this.packBoolean(byteBuffer, this.AgentData_Field.CastsShadows);
        byteBuffer.put((byte)this.ExtraPhysics_Fields.size());
        for (final ExtraPhysics extraPhysics : this.ExtraPhysics_Fields) {
            this.packByte(byteBuffer, (byte)extraPhysics.PhysicsShapeType);
            this.packFloat(byteBuffer, extraPhysics.Density);
            this.packFloat(byteBuffer, extraPhysics.Friction);
            this.packFloat(byteBuffer, extraPhysics.Restitution);
            this.packFloat(byteBuffer, extraPhysics.GravityMultiplier);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.ObjectLocalID = this.unpackInt(byteBuffer);
        this.AgentData_Field.UsePhysics = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.IsTemporary = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.IsPhantom = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.CastsShadows = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ExtraPhysics e = new ExtraPhysics();
            e.PhysicsShapeType = (this.unpackByte(byteBuffer) & 0xFF);
            e.Density = this.unpackFloat(byteBuffer);
            e.Friction = this.unpackFloat(byteBuffer);
            e.Restitution = this.unpackFloat(byteBuffer);
            e.GravityMultiplier = this.unpackFloat(byteBuffer);
            this.ExtraPhysics_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public boolean CastsShadows;
        public boolean IsPhantom;
        public boolean IsTemporary;
        public int ObjectLocalID;
        public UUID SessionID;
        public boolean UsePhysics;
    }
    
    public static class ExtraPhysics
    {
        public float Density;
        public float Friction;
        public float GravityMultiplier;
        public int PhysicsShapeType;
        public float Restitution;
    }
}
