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

public class UserReport extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ReportData
    {

        public UUID AbuseRegionID;
        public byte AbuseRegionName[];
        public UUID AbuserID;
        public int Category;
        public int CheckFlags;
        public byte Details[];
        public UUID ObjectID;
        public LLVector3 Position;
        public int ReportType;
        public UUID ScreenshotID;
        public byte Summary[];
        public byte VersionString[];

        public ReportData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ReportData ReportData_Field;

    public UserReport()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ReportData_Field = new ReportData();
    }

    public int CalcPayloadSize()
    {
        return ReportData_Field.AbuseRegionName.length + 64 + 16 + 1 + ReportData_Field.Summary.length + 2 + ReportData_Field.Details.length + 1 + ReportData_Field.VersionString.length + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUserReport(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-123);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packByte(bytebuffer, (byte)ReportData_Field.ReportType);
        packByte(bytebuffer, (byte)ReportData_Field.Category);
        packLLVector3(bytebuffer, ReportData_Field.Position);
        packByte(bytebuffer, (byte)ReportData_Field.CheckFlags);
        packUUID(bytebuffer, ReportData_Field.ScreenshotID);
        packUUID(bytebuffer, ReportData_Field.ObjectID);
        packUUID(bytebuffer, ReportData_Field.AbuserID);
        packVariable(bytebuffer, ReportData_Field.AbuseRegionName, 1);
        packUUID(bytebuffer, ReportData_Field.AbuseRegionID);
        packVariable(bytebuffer, ReportData_Field.Summary, 1);
        packVariable(bytebuffer, ReportData_Field.Details, 2);
        packVariable(bytebuffer, ReportData_Field.VersionString, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ReportData_Field.ReportType = unpackByte(bytebuffer) & 0xff;
        ReportData_Field.Category = unpackByte(bytebuffer) & 0xff;
        ReportData_Field.Position = unpackLLVector3(bytebuffer);
        ReportData_Field.CheckFlags = unpackByte(bytebuffer) & 0xff;
        ReportData_Field.ScreenshotID = unpackUUID(bytebuffer);
        ReportData_Field.ObjectID = unpackUUID(bytebuffer);
        ReportData_Field.AbuserID = unpackUUID(bytebuffer);
        ReportData_Field.AbuseRegionName = unpackVariable(bytebuffer, 1);
        ReportData_Field.AbuseRegionID = unpackUUID(bytebuffer);
        ReportData_Field.Summary = unpackVariable(bytebuffer, 1);
        ReportData_Field.Details = unpackVariable(bytebuffer, 2);
        ReportData_Field.VersionString = unpackVariable(bytebuffer, 1);
    }
}
