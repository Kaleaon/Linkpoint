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

public class AlertMessage extends SLMessage
{
    public static class AlertData
    {

        public byte Message[];

        public AlertData()
        {
        }
    }

    public static class AlertInfo
    {

        public byte ExtraParams[];
        public byte Message[];

        public AlertInfo()
        {
        }
    }


    public AlertData AlertData_Field;
    public ArrayList AlertInfo_Fields;

    public AlertMessage()
    {
        AlertInfo_Fields = new ArrayList();
        zeroCoded = false;
        AlertData_Field = new AlertData();
    }

    public int CalcPayloadSize()
    {
        int i = AlertData_Field.Message.length;
        Iterator iterator = AlertInfo_Fields.iterator();
        AlertInfo alertinfo;
        int j;
        for (i = i + 1 + 4 + 1; iterator.hasNext(); i = alertinfo.ExtraParams.length + (j + 1 + 1) + i)
        {
            alertinfo = (AlertInfo)iterator.next();
            j = alertinfo.Message.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAlertMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-122);
        packVariable(bytebuffer, AlertData_Field.Message, 1);
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
        AlertData_Field.Message = unpackVariable(bytebuffer, 1);
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
