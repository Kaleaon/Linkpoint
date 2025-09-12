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

public class MergeParcel extends SLMessage
{
    public static class MasterParcelData
    {

        public UUID MasterID;

        public MasterParcelData()
        {
        }
    }

    public static class SlaveParcelData
    {

        public UUID SlaveID;

        public SlaveParcelData()
        {
        }
    }


    public MasterParcelData MasterParcelData_Field;
    public ArrayList SlaveParcelData_Fields;

    public MergeParcel()
    {
        SlaveParcelData_Fields = new ArrayList();
        zeroCoded = false;
        MasterParcelData_Field = new MasterParcelData();
    }

    public int CalcPayloadSize()
    {
        return SlaveParcelData_Fields.size() * 16 + 21;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMergeParcel(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-33);
        packUUID(bytebuffer, MasterParcelData_Field.MasterID);
        bytebuffer.put((byte)SlaveParcelData_Fields.size());
        for (Iterator iterator = SlaveParcelData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((SlaveParcelData)iterator.next()).SlaveID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        MasterParcelData_Field.MasterID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            SlaveParcelData slaveparceldata = new SlaveParcelData();
            slaveparceldata.SlaveID = unpackUUID(bytebuffer);
            SlaveParcelData_Fields.add(slaveparceldata);
        }

    }
}
