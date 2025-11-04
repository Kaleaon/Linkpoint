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

public class ObjectGrabUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    public ArrayList<SurfaceInfo> SurfaceInfo_Fields;
    
    public ObjectGrabUpdate() {
        this.SurfaceInfo_Fields = new ArrayList<SurfaceInfo>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.SurfaceInfo_Fields.size() * 64 + 81;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectGrabUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)118);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        this.packLLVector3(byteBuffer, this.ObjectData_Field.GrabOffsetInitial);
        this.packLLVector3(byteBuffer, this.ObjectData_Field.GrabPosition);
        this.packInt(byteBuffer, this.ObjectData_Field.TimeSinceLast);
        byteBuffer.put((byte)this.SurfaceInfo_Fields.size());
        for (final SurfaceInfo surfaceInfo : this.SurfaceInfo_Fields) {
            this.packLLVector3(byteBuffer, surfaceInfo.UVCoord);
            this.packLLVector3(byteBuffer, surfaceInfo.STCoord);
            this.packInt(byteBuffer, surfaceInfo.FaceIndex);
            this.packLLVector3(byteBuffer, surfaceInfo.Position);
            this.packLLVector3(byteBuffer, surfaceInfo.Normal);
            this.packLLVector3(byteBuffer, surfaceInfo.Binormal);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.GrabOffsetInitial = this.unpackLLVector3(byteBuffer);
        this.ObjectData_Field.GrabPosition = this.unpackLLVector3(byteBuffer);
        this.ObjectData_Field.TimeSinceLast = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final SurfaceInfo e = new SurfaceInfo();
            e.UVCoord = this.unpackLLVector3(byteBuffer);
            e.STCoord = this.unpackLLVector3(byteBuffer);
            e.FaceIndex = this.unpackInt(byteBuffer);
            e.Position = this.unpackLLVector3(byteBuffer);
            e.Normal = this.unpackLLVector3(byteBuffer);
            e.Binormal = this.unpackLLVector3(byteBuffer);
            this.SurfaceInfo_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ObjectData
    {
        public LLVector3 GrabOffsetInitial;
        public LLVector3 GrabPosition;
        public UUID ObjectID;
        public int TimeSinceLast;
    }
    
    public static class SurfaceInfo
    {
        public LLVector3 Binormal;
        public int FaceIndex;
        public LLVector3 Normal;
        public LLVector3 Position;
        public LLVector3 STCoord;
        public LLVector3 UVCoord;
    }
}
