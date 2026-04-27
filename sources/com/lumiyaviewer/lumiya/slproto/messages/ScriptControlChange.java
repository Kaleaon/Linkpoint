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

public class ScriptControlChange extends SLMessage
{
    public static class Data
    {

        public int Controls;
        public boolean PassToAgent;
        public boolean TakeControls;

        public Data()
        {
        }
    }


    public ArrayList Data_Fields;

    public ScriptControlChange()
    {
        Data_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return Data_Fields.size() * 6 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptControlChange(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-67);
        bytebuffer.put((byte)Data_Fields.size());
        Data data;
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packBoolean(bytebuffer, data.PassToAgent))
        {
            data = (Data)iterator.next();
            packBoolean(bytebuffer, data.TakeControls);
            packInt(bytebuffer, data.Controls);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.TakeControls = unpackBoolean(bytebuffer);
            data.Controls = unpackInt(bytebuffer);
            data.PassToAgent = unpackBoolean(bytebuffer);
            Data_Fields.add(data);
        }

    }
}
