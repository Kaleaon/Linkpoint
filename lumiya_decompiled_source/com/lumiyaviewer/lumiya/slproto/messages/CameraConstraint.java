package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class CameraConstraint extends SLMessage {
    public CameraCollidePlane CameraCollidePlane_Field;

    /* loaded from: classes.dex */
    public static class CameraCollidePlane {
        public LLVector4 Plane;
    }

    public CameraConstraint() {
        this.zeroCoded = true;
        this.CameraCollidePlane_Field = new CameraCollidePlane();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 17;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCameraConstraint(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.SYN);
        packLLVector4(byteBuffer, this.CameraCollidePlane_Field.Plane);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.CameraCollidePlane_Field.Plane = unpackLLVector4(byteBuffer);
    }
}
