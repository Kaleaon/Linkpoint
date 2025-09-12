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

public class RequestImage extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RequestImageData
    {

        public int DiscardLevel;
        public float DownloadPriority;
        public UUID Image;
        public int Packet;
        public int Type;

        public RequestImageData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList RequestImageData_Fields;

    public RequestImage()
    {
        RequestImageData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return RequestImageData_Fields.size() * 26 + 34;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRequestImage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)8);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)RequestImageData_Fields.size());
        RequestImageData requestimagedata;
        for (Iterator iterator = RequestImageData_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)requestimagedata.Type))
        {
            requestimagedata = (RequestImageData)iterator.next();
            packUUID(bytebuffer, requestimagedata.Image);
            packByte(bytebuffer, (byte)requestimagedata.DiscardLevel);
            packFloat(bytebuffer, requestimagedata.DownloadPriority);
            packInt(bytebuffer, requestimagedata.Packet);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RequestImageData requestimagedata = new RequestImageData();
            requestimagedata.Image = unpackUUID(bytebuffer);
            requestimagedata.DiscardLevel = unpackByte(bytebuffer);
            requestimagedata.DownloadPriority = unpackFloat(bytebuffer);
            requestimagedata.Packet = unpackInt(bytebuffer);
            requestimagedata.Type = unpackByte(bytebuffer) & 0xff;
            RequestImageData_Fields.add(requestimagedata);
        }

    }
}
