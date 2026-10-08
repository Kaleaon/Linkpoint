package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class ObjectFlagUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<ExtraPhysics> ExtraPhysics_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public boolean CastsShadows;
        public boolean IsPhantom;
        public boolean IsTemporary;
        public int ObjectLocalID;
        public UUID SessionID;
        public boolean UsePhysics;
    }

    /* loaded from: classes.dex */
    public static class ExtraPhysics {
        public float Density;
        public float Friction;
        public float GravityMultiplier;
        public int PhysicsShapeType;
        public float Restitution;
    }

    public ObjectFlagUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.ExtraPhysics_Fields.size() * 17) + 45;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectFlagUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 94);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.ObjectLocalID);
        packBoolean(byteBuffer, this.AgentData_Field.UsePhysics);
        packBoolean(byteBuffer, this.AgentData_Field.IsTemporary);
        packBoolean(byteBuffer, this.AgentData_Field.IsPhantom);
        packBoolean(byteBuffer, this.AgentData_Field.CastsShadows);
        byteBuffer.put((byte) this.ExtraPhysics_Fields.size());
        for (ExtraPhysics extraPhysics : this.ExtraPhysics_Fields) {
            packByte(byteBuffer, (byte) extraPhysics.PhysicsShapeType);
            packFloat(byteBuffer, extraPhysics.Density);
            packFloat(byteBuffer, extraPhysics.Friction);
            packFloat(byteBuffer, extraPhysics.Restitution);
            packFloat(byteBuffer, extraPhysics.GravityMultiplier);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.ObjectLocalID = unpackInt(byteBuffer);
        this.AgentData_Field.UsePhysics = unpackBoolean(byteBuffer);
        this.AgentData_Field.IsTemporary = unpackBoolean(byteBuffer);
        this.AgentData_Field.IsPhantom = unpackBoolean(byteBuffer);
        this.AgentData_Field.CastsShadows = unpackBoolean(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            ExtraPhysics extraPhysics = new ExtraPhysics();
            extraPhysics.PhysicsShapeType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            extraPhysics.Density = unpackFloat(byteBuffer);
            extraPhysics.Friction = unpackFloat(byteBuffer);
            extraPhysics.Restitution = unpackFloat(byteBuffer);
            extraPhysics.GravityMultiplier = unpackFloat(byteBuffer);
            this.ExtraPhysics_Fields.add(extraPhysics);
        }
    }
}
