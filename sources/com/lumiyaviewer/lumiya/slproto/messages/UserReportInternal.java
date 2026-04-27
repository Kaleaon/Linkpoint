// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class UserReportInternal extends SLMessage
{
    public static class ReportData
    {

        public UUID AbuseRegionID;
        public byte AbuseRegionName[];
        public UUID AbuserID;
        public LLVector3 AgentPosition;
        public int Category;
        public UUID CreatorID;
        public byte Details[];
        public UUID LastOwnerID;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID RegionID;
        public int ReportType;
        public UUID ReporterID;
        public UUID ScreenshotID;
        public byte Summary[];
        public byte VersionString[];
        public LLVector3 ViewerPosition;

        public ReportData()
        {
        }
    }


    public ReportData ReportData_Field;

    public UserReportInternal()
    {
        zeroCoded = true;
        ReportData_Field = new ReportData();
    }

    public int CalcPayloadSize()
    {
        return ReportData_Field.AbuseRegionName.length + 155 + 16 + 1 + ReportData_Field.Summary.length + 2 + ReportData_Field.Details.length + 1 + ReportData_Field.VersionString.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUserReportInternal(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)21);
        packByte(bytebuffer, (byte)ReportData_Field.ReportType);
        packByte(bytebuffer, (byte)ReportData_Field.Category);
        packUUID(bytebuffer, ReportData_Field.ReporterID);
        packLLVector3(bytebuffer, ReportData_Field.ViewerPosition);
        packLLVector3(bytebuffer, ReportData_Field.AgentPosition);
        packUUID(bytebuffer, ReportData_Field.ScreenshotID);
        packUUID(bytebuffer, ReportData_Field.ObjectID);
        packUUID(bytebuffer, ReportData_Field.OwnerID);
        packUUID(bytebuffer, ReportData_Field.LastOwnerID);
        packUUID(bytebuffer, ReportData_Field.CreatorID);
        packUUID(bytebuffer, ReportData_Field.RegionID);
        packUUID(bytebuffer, ReportData_Field.AbuserID);
        packVariable(bytebuffer, ReportData_Field.AbuseRegionName, 1);
        packUUID(bytebuffer, ReportData_Field.AbuseRegionID);
        packVariable(bytebuffer, ReportData_Field.Summary, 1);
        packVariable(bytebuffer, ReportData_Field.Details, 2);
        packVariable(bytebuffer, ReportData_Field.VersionString, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ReportData_Field.ReportType = unpackByte(bytebuffer) & 0xff;
        ReportData_Field.Category = unpackByte(bytebuffer) & 0xff;
        ReportData_Field.ReporterID = unpackUUID(bytebuffer);
        ReportData_Field.ViewerPosition = unpackLLVector3(bytebuffer);
        ReportData_Field.AgentPosition = unpackLLVector3(bytebuffer);
        ReportData_Field.ScreenshotID = unpackUUID(bytebuffer);
        ReportData_Field.ObjectID = unpackUUID(bytebuffer);
        ReportData_Field.OwnerID = unpackUUID(bytebuffer);
        ReportData_Field.LastOwnerID = unpackUUID(bytebuffer);
        ReportData_Field.CreatorID = unpackUUID(bytebuffer);
        ReportData_Field.RegionID = unpackUUID(bytebuffer);
        ReportData_Field.AbuserID = unpackUUID(bytebuffer);
        ReportData_Field.AbuseRegionName = unpackVariable(bytebuffer, 1);
        ReportData_Field.AbuseRegionID = unpackUUID(bytebuffer);
        ReportData_Field.Summary = unpackVariable(bytebuffer, 1);
        ReportData_Field.Details = unpackVariable(bytebuffer, 2);
        ReportData_Field.VersionString = unpackVariable(bytebuffer, 1);
    }
}
