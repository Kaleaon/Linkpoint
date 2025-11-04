// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectShape extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public ObjectShape() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Fields.size() * 27 + 37;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectShape(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)98);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packInt(byteBuffer, objectData.ObjectLocalID);
            this.packByte(byteBuffer, (byte)objectData.PathCurve);
            this.packByte(byteBuffer, (byte)objectData.ProfileCurve);
            this.packShort(byteBuffer, (short)objectData.PathBegin);
            this.packShort(byteBuffer, (short)objectData.PathEnd);
            this.packByte(byteBuffer, (byte)objectData.PathScaleX);
            this.packByte(byteBuffer, (byte)objectData.PathScaleY);
            this.packByte(byteBuffer, (byte)objectData.PathShearX);
            this.packByte(byteBuffer, (byte)objectData.PathShearY);
            this.packByte(byteBuffer, (byte)objectData.PathTwist);
            this.packByte(byteBuffer, (byte)objectData.PathTwistBegin);
            this.packByte(byteBuffer, (byte)objectData.PathRadiusOffset);
            this.packByte(byteBuffer, (byte)objectData.PathTaperX);
            this.packByte(byteBuffer, (byte)objectData.PathTaperY);
            this.packByte(byteBuffer, (byte)objectData.PathRevolutions);
            this.packByte(byteBuffer, (byte)objectData.PathSkew);
            this.packShort(byteBuffer, (short)objectData.ProfileBegin);
            this.packShort(byteBuffer, (short)objectData.ProfileEnd);
            this.packShort(byteBuffer, (short)objectData.ProfileHollow);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ObjectLocalID = this.unpackInt(byteBuffer);
            e.PathCurve = (this.unpackByte(byteBuffer) & 0xFF);
            e.ProfileCurve = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathBegin = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.PathEnd = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.PathScaleX = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathScaleY = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathShearX = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathShearY = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathTwist = this.unpackByte(byteBuffer);
            e.PathTwistBegin = this.unpackByte(byteBuffer);
            e.PathRadiusOffset = this.unpackByte(byteBuffer);
            e.PathTaperX = this.unpackByte(byteBuffer);
            e.PathTaperY = this.unpackByte(byteBuffer);
            e.PathRevolutions = (this.unpackByte(byteBuffer) & 0xFF);
            e.PathSkew = this.unpackByte(byteBuffer);
            e.ProfileBegin = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.ProfileEnd = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.ProfileHollow = (this.unpackShort(byteBuffer) & 0xFFFF);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ObjectData
    {
        public int ObjectLocalID;
        public int PathBegin;
        public int PathCurve;
        public int PathEnd;
        public int PathRadiusOffset;
        public int PathRevolutions;
        public int PathScaleX;
        public int PathScaleY;
        public int PathShearX;
        public int PathShearY;
        public int PathSkew;
        public int PathTaperX;
        public int PathTaperY;
        public int PathTwist;
        public int PathTwistBegin;
        public int ProfileBegin;
        public int ProfileCurve;
        public int ProfileEnd;
        public int ProfileHollow;
    }
}
