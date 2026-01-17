package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class AvatarSitResponse extends SLMessage {
    public SitObject SitObject_Field;
    public SitTransform SitTransform_Field;

    /* loaded from: classes.dex */
    public static class SitObject {
        public UUID ID;
    }

    /* loaded from: classes.dex */
    public static class SitTransform {
        public boolean AutoPilot;
        public LLVector3 CameraAtOffset;
        public LLVector3 CameraEyeOffset;
        public boolean ForceMouselook;
        public LLVector3 SitPosition;
        public LLQuaternion SitRotation;
    }

    public AvatarSitResponse() {
        this.zeroCoded = true;
        this.SitObject_Field = new SitObject();
        this.SitTransform_Field = new SitTransform();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 67;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarSitResponse(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.NAK);
        packUUID(byteBuffer, this.SitObject_Field.ID);
        packBoolean(byteBuffer, this.SitTransform_Field.AutoPilot);
        packLLVector3(byteBuffer, this.SitTransform_Field.SitPosition);
        packLLQuaternion(byteBuffer, this.SitTransform_Field.SitRotation);
        packLLVector3(byteBuffer, this.SitTransform_Field.CameraEyeOffset);
        packLLVector3(byteBuffer, this.SitTransform_Field.CameraAtOffset);
        packBoolean(byteBuffer, this.SitTransform_Field.ForceMouselook);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SitObject_Field.ID = unpackUUID(byteBuffer);
        this.SitTransform_Field.AutoPilot = unpackBoolean(byteBuffer);
        this.SitTransform_Field.SitPosition = unpackLLVector3(byteBuffer);
        this.SitTransform_Field.SitRotation = unpackLLQuaternion(byteBuffer);
        this.SitTransform_Field.CameraEyeOffset = unpackLLVector3(byteBuffer);
        this.SitTransform_Field.CameraAtOffset = unpackLLVector3(byteBuffer);
        this.SitTransform_Field.ForceMouselook = unpackBoolean(byteBuffer);
    }
}
