// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AvatarSitResponse extends SLMessage
{
    public static class SitObject
    {

        public UUID ID;

        public SitObject()
        {
        }
    }

    public static class SitTransform
    {

        public boolean AutoPilot;
        public LLVector3 CameraAtOffset;
        public LLVector3 CameraEyeOffset;
        public boolean ForceMouselook;
        public LLVector3 SitPosition;
        public LLQuaternion SitRotation;

        public SitTransform()
        {
        }
    }


    public SitObject SitObject_Field;
    public SitTransform SitTransform_Field;

    public AvatarSitResponse()
    {
        zeroCoded = true;
        SitObject_Field = new SitObject();
        SitTransform_Field = new SitTransform();
    }

    public int CalcPayloadSize()
    {
        return 67;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarSitResponse(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)21);
        packUUID(bytebuffer, SitObject_Field.ID);
        packBoolean(bytebuffer, SitTransform_Field.AutoPilot);
        packLLVector3(bytebuffer, SitTransform_Field.SitPosition);
        packLLQuaternion(bytebuffer, SitTransform_Field.SitRotation);
        packLLVector3(bytebuffer, SitTransform_Field.CameraEyeOffset);
        packLLVector3(bytebuffer, SitTransform_Field.CameraAtOffset);
        packBoolean(bytebuffer, SitTransform_Field.ForceMouselook);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SitObject_Field.ID = unpackUUID(bytebuffer);
        SitTransform_Field.AutoPilot = unpackBoolean(bytebuffer);
        SitTransform_Field.SitPosition = unpackLLVector3(bytebuffer);
        SitTransform_Field.SitRotation = unpackLLQuaternion(bytebuffer);
        SitTransform_Field.CameraEyeOffset = unpackLLVector3(bytebuffer);
        SitTransform_Field.CameraAtOffset = unpackLLVector3(bytebuffer);
        SitTransform_Field.ForceMouselook = unpackBoolean(bytebuffer);
    }
}
