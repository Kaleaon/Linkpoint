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

public class TeleportFailed extends SLMessage
{
    public static class AlertInfo
    {

        public byte ExtraParams[];
        public byte Message[];

        public AlertInfo()
        {
        }
    }

    public static class Info
    {

        public UUID AgentID;
        public byte Reason[];

        public Info()
        {
        }
    }


    public ArrayList AlertInfo_Fields;
    public Info Info_Field;

    public TeleportFailed()
    {
        AlertInfo_Fields = new ArrayList();
        zeroCoded = false;
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        int i = Info_Field.Reason.length;
        Iterator iterator = AlertInfo_Fields.iterator();
        AlertInfo alertinfo;
        int j;
        for (i = i + 17 + 4 + 1; iterator.hasNext(); i = alertinfo.ExtraParams.length + (j + 1 + 1) + i)
        {
            alertinfo = (AlertInfo)iterator.next();
            j = alertinfo.Message.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportFailed(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)74);
        packUUID(bytebuffer, Info_Field.AgentID);
        packVariable(bytebuffer, Info_Field.Reason, 1);
        bytebuffer.put((byte)AlertInfo_Fields.size());
        AlertInfo alertinfo;
        for (Iterator iterator = AlertInfo_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, alertinfo.ExtraParams, 1))
        {
            alertinfo = (AlertInfo)iterator.next();
            packVariable(bytebuffer, alertinfo.Message, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.Reason = unpackVariable(bytebuffer, 1);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            AlertInfo alertinfo = new AlertInfo();
            alertinfo.Message = unpackVariable(bytebuffer, 1);
            alertinfo.ExtraParams = unpackVariable(bytebuffer, 1);
            AlertInfo_Fields.add(alertinfo);
        }

    }
}
