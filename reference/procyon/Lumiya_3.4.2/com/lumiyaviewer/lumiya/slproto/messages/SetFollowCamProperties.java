// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetFollowCamProperties extends SLMessage
{
    public ArrayList<CameraProperty> CameraProperty_Fields;
    public ObjectData ObjectData_Field;
    
    public SetFollowCamProperties() {
        this.CameraProperty_Fields = new ArrayList<CameraProperty>();
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.CameraProperty_Fields.size() * 8 + 21;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetFollowCamProperties(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-97));
        this.packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        byteBuffer.put((byte)this.CameraProperty_Fields.size());
        for (final CameraProperty cameraProperty : this.CameraProperty_Fields) {
            this.packInt(byteBuffer, cameraProperty.Type);
            this.packFloat(byteBuffer, cameraProperty.Value);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final CameraProperty e = new CameraProperty();
            e.Type = this.unpackInt(byteBuffer);
            e.Value = this.unpackFloat(byteBuffer);
            this.CameraProperty_Fields.add(e);
        }
    }
    
    public static class CameraProperty
    {
        public int Type;
        public float Value;
    }
    
    public static class ObjectData
    {
        public UUID ObjectID;
    }
}
