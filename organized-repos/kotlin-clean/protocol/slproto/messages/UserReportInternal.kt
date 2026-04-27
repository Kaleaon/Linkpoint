package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class UserReportInternal : SLMessage() {
    public ReportData ReportData_Field = ReportData()

    @JvmStatic
    class ReportData {
        public UUID AbuseRegionID
        public ByteArray AbuseRegionName
        public UUID AbuserID
        public LLVector3 AgentPosition
        public Int Category
        public UUID CreatorID
        public ByteArray Details
        public UUID LastOwnerID
        public UUID ObjectID
        public UUID OwnerID
        public UUID RegionID
        public Int ReportType
        public UUID ReporterID
        public UUID ScreenshotID
        public ByteArray Summary
        public ByteArray VersionString
        public LLVector3 ViewerPosition
    }

    public UserReportInternal() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.ReportData_Field.AbuseRegionName.length + 155 + 16 + 1 + this.ReportData_Field.Summary.length + 2 + this.ReportData_Field.Details.length + 1 + this.ReportData_Field.VersionString.length + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserReportInternal(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.NAK)
        packByte(byteBuffer, (Byte) this.ReportData_Field.ReportType)
        packByte(byteBuffer, (Byte) this.ReportData_Field.Category)
        packUUID(byteBuffer, this.ReportData_Field.ReporterID)
        packLLVector3(byteBuffer, this.ReportData_Field.ViewerPosition)
        packLLVector3(byteBuffer, this.ReportData_Field.AgentPosition)
        packUUID(byteBuffer, this.ReportData_Field.ScreenshotID)
        packUUID(byteBuffer, this.ReportData_Field.ObjectID)
        packUUID(byteBuffer, this.ReportData_Field.OwnerID)
        packUUID(byteBuffer, this.ReportData_Field.LastOwnerID)
        packUUID(byteBuffer, this.ReportData_Field.CreatorID)
        packUUID(byteBuffer, this.ReportData_Field.RegionID)
        packUUID(byteBuffer, this.ReportData_Field.AbuserID)
        packVariable(byteBuffer, this.ReportData_Field.AbuseRegionName, 1)
        packUUID(byteBuffer, this.ReportData_Field.AbuseRegionID)
        packVariable(byteBuffer, this.ReportData_Field.Summary, 1)
        packVariable(byteBuffer, this.ReportData_Field.Details, 2)
        packVariable(byteBuffer, this.ReportData_Field.VersionString, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.ReportData_Field.ReportType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ReportData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ReportData_Field.ReporterID = unpackUUID(byteBuffer)
        this.ReportData_Field.ViewerPosition = unpackLLVector3(byteBuffer)
        this.ReportData_Field.AgentPosition = unpackLLVector3(byteBuffer)
        this.ReportData_Field.ScreenshotID = unpackUUID(byteBuffer)
        this.ReportData_Field.ObjectID = unpackUUID(byteBuffer)
        this.ReportData_Field.OwnerID = unpackUUID(byteBuffer)
        this.ReportData_Field.LastOwnerID = unpackUUID(byteBuffer)
        this.ReportData_Field.CreatorID = unpackUUID(byteBuffer)
        this.ReportData_Field.RegionID = unpackUUID(byteBuffer)
        this.ReportData_Field.AbuserID = unpackUUID(byteBuffer)
        this.ReportData_Field.AbuseRegionName = unpackVariable(byteBuffer, 1)
        this.ReportData_Field.AbuseRegionID = unpackUUID(byteBuffer)
        this.ReportData_Field.Summary = unpackVariable(byteBuffer, 1)
        this.ReportData_Field.Details = unpackVariable(byteBuffer, 2)
        this.ReportData_Field.VersionString = unpackVariable(byteBuffer, 1)
    }
}
