package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class UserReport extends SLMessage {
    public AgentData AgentData_Field;
    public ReportData ReportData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class ReportData {
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

    public UserReport() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ReportData_Field = new ReportData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.ReportData_Field.AbuseRegionName.length + 64 + 16 + 1 + this.ReportData_Field.Summary.length + 2 + this.ReportData_Field.Details.length + 1 + this.ReportData_Field.VersionString.length + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserReport(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -123);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packByte(byteBuffer, (byte) this.ReportData_Field.ReportType);
        packByte(byteBuffer, (byte) this.ReportData_Field.Category);
        packLLVector3(byteBuffer, this.ReportData_Field.Position);
        packByte(byteBuffer, (byte) this.ReportData_Field.CheckFlags);
        packUUID(byteBuffer, this.ReportData_Field.ScreenshotID);
        packUUID(byteBuffer, this.ReportData_Field.ObjectID);
        packUUID(byteBuffer, this.ReportData_Field.AbuserID);
        packVariable(byteBuffer, this.ReportData_Field.AbuseRegionName, 1);
        packUUID(byteBuffer, this.ReportData_Field.AbuseRegionID);
        packVariable(byteBuffer, this.ReportData_Field.Summary, 1);
        packVariable(byteBuffer, this.ReportData_Field.Details, 2);
        packVariable(byteBuffer, this.ReportData_Field.VersionString, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ReportData_Field.ReportType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ReportData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ReportData_Field.Position = unpackLLVector3(byteBuffer);
        this.ReportData_Field.CheckFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ReportData_Field.ScreenshotID = unpackUUID(byteBuffer);
        this.ReportData_Field.ObjectID = unpackUUID(byteBuffer);
        this.ReportData_Field.AbuserID = unpackUUID(byteBuffer);
        this.ReportData_Field.AbuseRegionName = unpackVariable(byteBuffer, 1);
        this.ReportData_Field.AbuseRegionID = unpackUUID(byteBuffer);
        this.ReportData_Field.Summary = unpackVariable(byteBuffer, 1);
        this.ReportData_Field.Details = unpackVariable(byteBuffer, 2);
        this.ReportData_Field.VersionString = unpackVariable(byteBuffer, 1);
    }
}
