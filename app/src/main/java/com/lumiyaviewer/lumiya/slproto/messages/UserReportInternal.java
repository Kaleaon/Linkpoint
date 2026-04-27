package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UserReportInternal extends SLMessage {
    public ReportData ReportData_Field = new ReportData();

    public static class ReportData {
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

    public UserReportInternal() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.ReportData_Field.AbuseRegionName.length + 155 + 16 + 1 + this.ReportData_Field.Summary.length + 2 + this.ReportData_Field.Details.length + 1 + this.ReportData_Field.VersionString.length + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserReportInternal(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.NAK);
        packByte(byteBuffer, (byte) this.ReportData_Field.ReportType);
        packByte(byteBuffer, (byte) this.ReportData_Field.Category);
        packUUID(byteBuffer, this.ReportData_Field.ReporterID);
        packLLVector3(byteBuffer, this.ReportData_Field.ViewerPosition);
        packLLVector3(byteBuffer, this.ReportData_Field.AgentPosition);
        packUUID(byteBuffer, this.ReportData_Field.ScreenshotID);
        packUUID(byteBuffer, this.ReportData_Field.ObjectID);
        packUUID(byteBuffer, this.ReportData_Field.OwnerID);
        packUUID(byteBuffer, this.ReportData_Field.LastOwnerID);
        packUUID(byteBuffer, this.ReportData_Field.CreatorID);
        packUUID(byteBuffer, this.ReportData_Field.RegionID);
        packUUID(byteBuffer, this.ReportData_Field.AbuserID);
        packVariable(byteBuffer, this.ReportData_Field.AbuseRegionName, 1);
        packUUID(byteBuffer, this.ReportData_Field.AbuseRegionID);
        packVariable(byteBuffer, this.ReportData_Field.Summary, 1);
        packVariable(byteBuffer, this.ReportData_Field.Details, 2);
        packVariable(byteBuffer, this.ReportData_Field.VersionString, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ReportData_Field.ReportType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ReportData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ReportData_Field.ReporterID = unpackUUID(byteBuffer);
        this.ReportData_Field.ViewerPosition = unpackLLVector3(byteBuffer);
        this.ReportData_Field.AgentPosition = unpackLLVector3(byteBuffer);
        this.ReportData_Field.ScreenshotID = unpackUUID(byteBuffer);
        this.ReportData_Field.ObjectID = unpackUUID(byteBuffer);
        this.ReportData_Field.OwnerID = unpackUUID(byteBuffer);
        this.ReportData_Field.LastOwnerID = unpackUUID(byteBuffer);
        this.ReportData_Field.CreatorID = unpackUUID(byteBuffer);
        this.ReportData_Field.RegionID = unpackUUID(byteBuffer);
        this.ReportData_Field.AbuserID = unpackUUID(byteBuffer);
        this.ReportData_Field.AbuseRegionName = unpackVariable(byteBuffer, 1);
        this.ReportData_Field.AbuseRegionID = unpackUUID(byteBuffer);
        this.ReportData_Field.Summary = unpackVariable(byteBuffer, 1);
        this.ReportData_Field.Details = unpackVariable(byteBuffer, 2);
        this.ReportData_Field.VersionString = unpackVariable(byteBuffer, 1);
    }
}
