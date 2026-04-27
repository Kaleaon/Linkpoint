// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector4;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CameraConstraint extends SLMessage
{
    public CameraCollidePlane CameraCollidePlane_Field;
    
    public CameraConstraint() {
        this.zeroCoded = true;
        this.CameraCollidePlane_Field = new CameraCollidePlane();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 17;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCameraConstraint(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)22);
        this.packLLVector4(byteBuffer, this.CameraCollidePlane_Field.Plane);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.CameraCollidePlane_Field.Plane = this.unpackLLVector4(byteBuffer);
    }
    
    public static class CameraCollidePlane
    {
        public LLVector4 Plane;
    }
}
