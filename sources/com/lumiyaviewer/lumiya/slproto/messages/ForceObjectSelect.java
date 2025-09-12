// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ForceObjectSelect extends SLMessage
{
    public static class Data
    {

        public int LocalID;

        public Data()
        {
        }
    }

    public static class Header
    {

        public boolean ResetList;

        public Header()
        {
        }
    }


    public ArrayList Data_Fields;
    public Header Header_Field;

    public ForceObjectSelect()
    {
        Data_Fields = new ArrayList();
        zeroCoded = false;
        Header_Field = new Header();
    }

    public int CalcPayloadSize()
    {
        return Data_Fields.size() * 4 + 6;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleForceObjectSelect(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-51);
        packBoolean(bytebuffer, Header_Field.ResetList);
        bytebuffer.put((byte)Data_Fields.size());
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, ((Data)iterator.next()).LocalID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Header_Field.ResetList = unpackBoolean(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.LocalID = unpackInt(bytebuffer);
            Data_Fields.add(data);
        }

    }
}
