package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AvatarSitResponse extends SLMessage {
    public SitObject SitObject_Field = new SitObject();
    public SitTransform SitTransform_Field = new SitTransform();

    public static class SitObject {
        public UUID ID;
    }

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
    }

    public int CalcPayloadSize() {
        return 67;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarSitResponse(this);
    }

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
