// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectDuplicateOnRay extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public ObjectDuplicateOnRay() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Fields.size() * 4 + 101;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectDuplicateOnRay(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)91);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packLLVector3(byteBuffer, this.AgentData_Field.RayStart);
        this.packLLVector3(byteBuffer, this.AgentData_Field.RayEnd);
        this.packBoolean(byteBuffer, this.AgentData_Field.BypassRaycast);
        this.packBoolean(byteBuffer, this.AgentData_Field.RayEndIsIntersection);
        this.packBoolean(byteBuffer, this.AgentData_Field.CopyCenters);
        this.packBoolean(byteBuffer, this.AgentData_Field.CopyRotates);
        this.packUUID(byteBuffer, this.AgentData_Field.RayTargetID);
        this.packInt(byteBuffer, this.AgentData_Field.DuplicateFlags);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packInt(byteBuffer, iterator.next().ObjectLocalID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.RayStart = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.RayEnd = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.BypassRaycast = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.RayEndIsIntersection = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.CopyCenters = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.CopyRotates = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.RayTargetID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.DuplicateFlags = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ObjectLocalID = this.unpackInt(byteBuffer);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public boolean BypassRaycast;
        public boolean CopyCenters;
        public boolean CopyRotates;
        public int DuplicateFlags;
        public UUID GroupID;
        public LLVector3 RayEnd;
        public boolean RayEndIsIntersection;
        public LLVector3 RayStart;
        public UUID RayTargetID;
        public UUID SessionID;
    }
    
    public static class ObjectData
    {
        public int ObjectLocalID;
    }
}
