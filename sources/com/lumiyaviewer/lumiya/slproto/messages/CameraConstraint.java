// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CameraConstraint extends SLMessage
{
    public static class CameraCollidePlane
    {

        public LLVector4 Plane;

        public CameraCollidePlane()
        {
        }
    }


    public CameraCollidePlane CameraCollidePlane_Field;

    public CameraConstraint()
    {
        zeroCoded = true;
        CameraCollidePlane_Field = new CameraCollidePlane();
    }

    public int CalcPayloadSize()
    {
        return 17;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCameraConstraint(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)22);
        packLLVector4(bytebuffer, CameraCollidePlane_Field.Plane);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        CameraCollidePlane_Field.Plane = unpackLLVector4(bytebuffer);
    }
}
