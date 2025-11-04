// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptSensorReply extends SLMessage
{
    public Requester Requester_Field;
    public ArrayList<SensedData> SensedData_Fields;
    
    public ScriptSensorReply() {
        this.SensedData_Fields = new ArrayList<SensedData>();
        this.zeroCoded = true;
        this.Requester_Field = new Requester();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.SensedData_Fields.iterator();
        int n = 21;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 85 + 4 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptSensorReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-8));
        this.packUUID(byteBuffer, this.Requester_Field.SourceID);
        byteBuffer.put((byte)this.SensedData_Fields.size());
        for (final SensedData sensedData : this.SensedData_Fields) {
            this.packUUID(byteBuffer, sensedData.ObjectID);
            this.packUUID(byteBuffer, sensedData.OwnerID);
            this.packUUID(byteBuffer, sensedData.GroupID);
            this.packLLVector3(byteBuffer, sensedData.Position);
            this.packLLVector3(byteBuffer, sensedData.Velocity);
            this.packLLQuaternion(byteBuffer, sensedData.Rotation);
            this.packVariable(byteBuffer, sensedData.Name, 1);
            this.packInt(byteBuffer, sensedData.Type);
            this.packFloat(byteBuffer, sensedData.Range);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Requester_Field.SourceID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final SensedData e = new SensedData();
            e.ObjectID = this.unpackUUID(byteBuffer);
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.GroupID = this.unpackUUID(byteBuffer);
            e.Position = this.unpackLLVector3(byteBuffer);
            e.Velocity = this.unpackLLVector3(byteBuffer);
            e.Rotation = this.unpackLLQuaternion(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Type = this.unpackInt(byteBuffer);
            e.Range = this.unpackFloat(byteBuffer);
            this.SensedData_Fields.add(e);
        }
    }
    
    public static class Requester
    {
        public UUID SourceID;
    }
    
    public static class SensedData
    {
        public UUID GroupID;
        public byte[] Name;
        public UUID ObjectID;
        public UUID OwnerID;
        public LLVector3 Position;
        public float Range;
        public LLQuaternion Rotation;
        public int Type;
        public LLVector3 Velocity;
    }
}
