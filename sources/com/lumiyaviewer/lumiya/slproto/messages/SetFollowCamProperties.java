// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SetFollowCamProperties extends SLMessage
{
    public static class CameraProperty
    {

        public int Type;
        public float Value;

        public CameraProperty()
        {
        }
    }

    public static class ObjectData
    {

        public UUID ObjectID;

        public ObjectData()
        {
        }
    }


    public ArrayList CameraProperty_Fields;
    public ObjectData ObjectData_Field;

    public SetFollowCamProperties()
    {
        CameraProperty_Fields = new ArrayList();
        zeroCoded = false;
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return CameraProperty_Fields.size() * 8 + 21;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSetFollowCamProperties(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-97);
        packUUID(bytebuffer, ObjectData_Field.ObjectID);
        bytebuffer.put((byte)CameraProperty_Fields.size());
        CameraProperty cameraproperty;
        for (Iterator iterator = CameraProperty_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, cameraproperty.Value))
        {
            cameraproperty = (CameraProperty)iterator.next();
            packInt(bytebuffer, cameraproperty.Type);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ObjectData_Field.ObjectID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            CameraProperty cameraproperty = new CameraProperty();
            cameraproperty.Type = unpackInt(bytebuffer);
            cameraproperty.Value = unpackFloat(bytebuffer);
            CameraProperty_Fields.add(cameraproperty);
        }

    }
}
