// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarSitResponse extends SLMessage
{
    public SitObject SitObject_Field;
    public SitTransform SitTransform_Field;
    
    public AvatarSitResponse() {
        this.zeroCoded = true;
        this.SitObject_Field = new SitObject();
        this.SitTransform_Field = new SitTransform();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 67;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarSitResponse(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)21);
        this.packUUID(byteBuffer, this.SitObject_Field.ID);
        this.packBoolean(byteBuffer, this.SitTransform_Field.AutoPilot);
        this.packLLVector3(byteBuffer, this.SitTransform_Field.SitPosition);
        this.packLLQuaternion(byteBuffer, this.SitTransform_Field.SitRotation);
        this.packLLVector3(byteBuffer, this.SitTransform_Field.CameraEyeOffset);
        this.packLLVector3(byteBuffer, this.SitTransform_Field.CameraAtOffset);
        this.packBoolean(byteBuffer, this.SitTransform_Field.ForceMouselook);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SitObject_Field.ID = this.unpackUUID(byteBuffer);
        this.SitTransform_Field.AutoPilot = this.unpackBoolean(byteBuffer);
        this.SitTransform_Field.SitPosition = this.unpackLLVector3(byteBuffer);
        this.SitTransform_Field.SitRotation = this.unpackLLQuaternion(byteBuffer);
        this.SitTransform_Field.CameraEyeOffset = this.unpackLLVector3(byteBuffer);
        this.SitTransform_Field.CameraAtOffset = this.unpackLLVector3(byteBuffer);
        this.SitTransform_Field.ForceMouselook = this.unpackBoolean(byteBuffer);
    }
    
    public static class SitObject
    {
        public UUID ID;
    }
    
    public static class SitTransform
    {
        public boolean AutoPilot;
        public LLVector3 CameraAtOffset;
        public LLVector3 CameraEyeOffset;
        public boolean ForceMouselook;
        public LLVector3 SitPosition;
        public LLQuaternion SitRotation;
    }
}
