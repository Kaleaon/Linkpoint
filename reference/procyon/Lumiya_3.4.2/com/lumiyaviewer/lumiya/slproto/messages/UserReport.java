// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UserReport extends SLMessage
{
    public AgentData AgentData_Field;
    public ReportData ReportData_Field;
    
    public UserReport() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ReportData_Field = new ReportData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ReportData_Field.AbuseRegionName.length + 64 + 16 + 1 + this.ReportData_Field.Summary.length + 2 + this.ReportData_Field.Details.length + 1 + this.ReportData_Field.VersionString.length + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUserReport(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-123));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packByte(byteBuffer, (byte)this.ReportData_Field.ReportType);
        this.packByte(byteBuffer, (byte)this.ReportData_Field.Category);
        this.packLLVector3(byteBuffer, this.ReportData_Field.Position);
        this.packByte(byteBuffer, (byte)this.ReportData_Field.CheckFlags);
        this.packUUID(byteBuffer, this.ReportData_Field.ScreenshotID);
        this.packUUID(byteBuffer, this.ReportData_Field.ObjectID);
        this.packUUID(byteBuffer, this.ReportData_Field.AbuserID);
        this.packVariable(byteBuffer, this.ReportData_Field.AbuseRegionName, 1);
        this.packUUID(byteBuffer, this.ReportData_Field.AbuseRegionID);
        this.packVariable(byteBuffer, this.ReportData_Field.Summary, 1);
        this.packVariable(byteBuffer, this.ReportData_Field.Details, 2);
        this.packVariable(byteBuffer, this.ReportData_Field.VersionString, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.ReportType = (this.unpackByte(byteBuffer) & 0xFF);
        this.ReportData_Field.Category = (this.unpackByte(byteBuffer) & 0xFF);
        this.ReportData_Field.Position = this.unpackLLVector3(byteBuffer);
        this.ReportData_Field.CheckFlags = (this.unpackByte(byteBuffer) & 0xFF);
        this.ReportData_Field.ScreenshotID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.AbuserID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.AbuseRegionName = this.unpackVariable(byteBuffer, 1);
        this.ReportData_Field.AbuseRegionID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.Summary = this.unpackVariable(byteBuffer, 1);
        this.ReportData_Field.Details = this.unpackVariable(byteBuffer, 2);
        this.ReportData_Field.VersionString = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ReportData
    {
        public UUID AbuseRegionID;
        public byte[] AbuseRegionName;
        public UUID AbuserID;
        public int Category;
        public int CheckFlags;
        public byte[] Details;
        public UUID ObjectID;
        public LLVector3 Position;
        public int ReportType;
        public UUID ScreenshotID;
        public byte[] Summary;
        public byte[] VersionString;
    }
}
