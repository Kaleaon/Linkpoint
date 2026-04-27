package com.linkpoint.slproto.messages;

import com.google.common.base.Ascii;
import com.linkpoint.slproto.SLMessage;
import com.linkpoint.slproto.types.LLVector4;
import java.nio.ByteBuffer;

public class CameraConstraint extends SLMessage {
    public CameraCollidePlane CameraCollidePlane_Field = new CameraCollidePlane();

    public static class CameraCollidePlane {
        public LLVector4 Plane;
    }

    public CameraConstraint() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 17;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCameraConstraint(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.SYN);
        packLLVector4(byteBuffer, this.CameraCollidePlane_Field.Plane);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.CameraCollidePlane_Field.Plane = unpackLLVector4(byteBuffer);
    }
}
