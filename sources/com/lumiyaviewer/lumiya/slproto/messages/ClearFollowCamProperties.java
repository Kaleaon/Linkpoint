// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ClearFollowCamProperties extends SLMessage
{
    public static class ObjectData
    {

        public UUID ObjectID;

        public ObjectData()
        {
        }
    }


    public ObjectData ObjectData_Field;

    public ClearFollowCamProperties()
    {
        zeroCoded = false;
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleClearFollowCamProperties(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-96);
        packUUID(bytebuffer, ObjectData_Field.ObjectID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ObjectData_Field.ObjectID = unpackUUID(bytebuffer);
    }
}
