package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class UserReport : SLMessage {
    AgentData AgentData_Field = AgentData()
    ReportData ReportData_Field = ReportData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ReportData {
        UUID AbuseRegionID
        ByteArray AbuseRegionName
        UUID AbuserID
        Int Category
        Int CheckFlags
        ByteArray Details
        UUID ObjectID
        LLVector3 Position
        Int ReportType
        UUID ScreenshotID
        ByteArray Summary
        ByteArray VersionString
    }

    UserReport() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.ReportData_Field.AbuseRegionName.length + 64 + 16 + 1 + this.ReportData_Field.Summary.length + 2 + this.ReportData_Field.Details.length + 1 + this.ReportData_Field.VersionString.length + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserReport(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -123)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (Byte) this.ReportData_Field.ReportType)
        packByte(byteBuffer, (Byte) this.ReportData_Field.Category)
        packLLVector3(byteBuffer, this.ReportData_Field.Position)
        packByte(byteBuffer, (Byte) this.ReportData_Field.CheckFlags)
        packUUID(byteBuffer, this.ReportData_Field.ScreenshotID)
        packUUID(byteBuffer, this.ReportData_Field.ObjectID)
        packUUID(byteBuffer, this.ReportData_Field.AbuserID)
        packVariable(byteBuffer, this.ReportData_Field.AbuseRegionName, 1)
        packUUID(byteBuffer, this.ReportData_Field.AbuseRegionID)
        packVariable(byteBuffer, this.ReportData_Field.Summary, 1)
        packVariable(byteBuffer, this.ReportData_Field.Details, 2)
        packVariable(byteBuffer, this.ReportData_Field.VersionString, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ReportData_Field.ReportType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ReportData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ReportData_Field.Position = unpackLLVector3(byteBuffer)
        this.ReportData_Field.CheckFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ReportData_Field.ScreenshotID = unpackUUID(byteBuffer)
        this.ReportData_Field.ObjectID = unpackUUID(byteBuffer)
        this.ReportData_Field.AbuserID = unpackUUID(byteBuffer)
        this.ReportData_Field.AbuseRegionName = unpackVariable(byteBuffer, 1)
        this.ReportData_Field.AbuseRegionID = unpackUUID(byteBuffer)
        this.ReportData_Field.Summary = unpackVariable(byteBuffer, 1)
        this.ReportData_Field.Details = unpackVariable(byteBuffer, 2)
        this.ReportData_Field.VersionString = unpackVariable(byteBuffer, 1)
    }
}
