// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UserReportInternal extends SLMessage
{
    public ReportData ReportData_Field;
    
    public UserReportInternal() {
        this.zeroCoded = true;
        this.ReportData_Field = new ReportData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ReportData_Field.AbuseRegionName.length + 155 + 16 + 1 + this.ReportData_Field.Summary.length + 2 + this.ReportData_Field.Details.length + 1 + this.ReportData_Field.VersionString.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUserReportInternal(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)21);
        this.packByte(byteBuffer, (byte)this.ReportData_Field.ReportType);
        this.packByte(byteBuffer, (byte)this.ReportData_Field.Category);
        this.packUUID(byteBuffer, this.ReportData_Field.ReporterID);
        this.packLLVector3(byteBuffer, this.ReportData_Field.ViewerPosition);
        this.packLLVector3(byteBuffer, this.ReportData_Field.AgentPosition);
        this.packUUID(byteBuffer, this.ReportData_Field.ScreenshotID);
        this.packUUID(byteBuffer, this.ReportData_Field.ObjectID);
        this.packUUID(byteBuffer, this.ReportData_Field.OwnerID);
        this.packUUID(byteBuffer, this.ReportData_Field.LastOwnerID);
        this.packUUID(byteBuffer, this.ReportData_Field.CreatorID);
        this.packUUID(byteBuffer, this.ReportData_Field.RegionID);
        this.packUUID(byteBuffer, this.ReportData_Field.AbuserID);
        this.packVariable(byteBuffer, this.ReportData_Field.AbuseRegionName, 1);
        this.packUUID(byteBuffer, this.ReportData_Field.AbuseRegionID);
        this.packVariable(byteBuffer, this.ReportData_Field.Summary, 1);
        this.packVariable(byteBuffer, this.ReportData_Field.Details, 2);
        this.packVariable(byteBuffer, this.ReportData_Field.VersionString, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ReportData_Field.ReportType = (this.unpackByte(byteBuffer) & 0xFF);
        this.ReportData_Field.Category = (this.unpackByte(byteBuffer) & 0xFF);
        this.ReportData_Field.ReporterID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.ViewerPosition = this.unpackLLVector3(byteBuffer);
        this.ReportData_Field.AgentPosition = this.unpackLLVector3(byteBuffer);
        this.ReportData_Field.ScreenshotID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.LastOwnerID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.CreatorID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.RegionID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.AbuserID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.AbuseRegionName = this.unpackVariable(byteBuffer, 1);
        this.ReportData_Field.AbuseRegionID = this.unpackUUID(byteBuffer);
        this.ReportData_Field.Summary = this.unpackVariable(byteBuffer, 1);
        this.ReportData_Field.Details = this.unpackVariable(byteBuffer, 2);
        this.ReportData_Field.VersionString = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class ReportData
    {
        public UUID AbuseRegionID;
        public byte[] AbuseRegionName;
        public UUID AbuserID;
        public LLVector3 AgentPosition;
        public int Category;
        public UUID CreatorID;
        public byte[] Details;
        public UUID LastOwnerID;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID RegionID;
        public int ReportType;
        public UUID ReporterID;
        public UUID ScreenshotID;
        public byte[] Summary;
        public byte[] VersionString;
        public LLVector3 ViewerPosition;
    }
}
