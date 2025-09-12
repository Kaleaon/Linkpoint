// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DetachAttachmentIntoInv extends SLMessage
{
    public static class ObjectData
    {

        public UUID AgentID;
        public UUID ItemID;

        public ObjectData()
        {
        }
    }


    public ObjectData ObjectData_Field;

    public DetachAttachmentIntoInv()
    {
        zeroCoded = false;
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDetachAttachmentIntoInv(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-115);
        packUUID(bytebuffer, ObjectData_Field.AgentID);
        packUUID(bytebuffer, ObjectData_Field.ItemID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ObjectData_Field.AgentID = unpackUUID(bytebuffer);
        ObjectData_Field.ItemID = unpackUUID(bytebuffer);
    }
}
