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

public class LandStatReply extends SLMessage
{
    public static class ReportData
    {

        public float LocationX;
        public float LocationY;
        public float LocationZ;
        public byte OwnerName[];
        public float Score;
        public UUID TaskID;
        public int TaskLocalID;
        public byte TaskName[];

        public ReportData()
        {
        }
    }

    public static class RequestData
    {

        public int ReportType;
        public int RequestFlags;
        public int TotalObjectCount;

        public RequestData()
        {
        }
    }


    public ArrayList ReportData_Fields;
    public RequestData RequestData_Field;

    public LandStatReply()
    {
        ReportData_Fields = new ArrayList();
        zeroCoded = false;
        RequestData_Field = new RequestData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ReportData_Fields.iterator();
        ReportData reportdata;
        int i;
        int j;
        for (i = 17; iterator.hasNext(); i = reportdata.OwnerName.length + (j + 37 + 1) + i)
        {
            reportdata = (ReportData)iterator.next();
            j = reportdata.TaskName.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLandStatReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-90);
        packInt(bytebuffer, RequestData_Field.ReportType);
        packInt(bytebuffer, RequestData_Field.RequestFlags);
        packInt(bytebuffer, RequestData_Field.TotalObjectCount);
        bytebuffer.put((byte)ReportData_Fields.size());
        ReportData reportdata;
        for (Iterator iterator = ReportData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, reportdata.OwnerName, 1))
        {
            reportdata = (ReportData)iterator.next();
            packInt(bytebuffer, reportdata.TaskLocalID);
            packUUID(bytebuffer, reportdata.TaskID);
            packFloat(bytebuffer, reportdata.LocationX);
            packFloat(bytebuffer, reportdata.LocationY);
            packFloat(bytebuffer, reportdata.LocationZ);
            packFloat(bytebuffer, reportdata.Score);
            packVariable(bytebuffer, reportdata.TaskName, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RequestData_Field.ReportType = unpackInt(bytebuffer);
        RequestData_Field.RequestFlags = unpackInt(bytebuffer);
        RequestData_Field.TotalObjectCount = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ReportData reportdata = new ReportData();
            reportdata.TaskLocalID = unpackInt(bytebuffer);
            reportdata.TaskID = unpackUUID(bytebuffer);
            reportdata.LocationX = unpackFloat(bytebuffer);
            reportdata.LocationY = unpackFloat(bytebuffer);
            reportdata.LocationZ = unpackFloat(bytebuffer);
            reportdata.Score = unpackFloat(bytebuffer);
            reportdata.TaskName = unpackVariable(bytebuffer, 1);
            reportdata.OwnerName = unpackVariable(bytebuffer, 1);
            ReportData_Fields.add(reportdata);
        }

    }
}
