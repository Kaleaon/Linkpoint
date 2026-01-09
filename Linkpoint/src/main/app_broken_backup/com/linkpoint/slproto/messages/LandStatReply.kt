package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class LandStatReply : SLMessage {
    ArrayList<ReportData> ReportData_Fields = ArrayList<>()
    RequestData RequestData_Field

    class ReportData {
        float LocationX
        float LocationY
        float LocationZ
        ByteArray OwnerName
        float Score
        UUID TaskID
        Int TaskLocalID
        ByteArray TaskName
    }

    class RequestData {
        Int ReportType
        Int RequestFlags
        Int TotalObjectCount
    }

    LandStatReply() {
        this.zeroCoded = false
        this.RequestData_Field = RequestData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 17
        Iterator<T> it = this.ReportData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            ReportData reportData = (it as ReportData).next()
            i = reportData.OwnerName.size + reportData.TaskName.size + 37 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleLandStatReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -90)
        packInt(byteBuffer, this.RequestData_Field.ReportType)
        packInt(byteBuffer, this.RequestData_Field.RequestFlags)
        packInt(byteBuffer, this.RequestData_Field.TotalObjectCount)
        byteBuffer.put((this as byte).ReportData_Fields.size())
        for (ReportData reportData : this.ReportData_Fields) {
            packInt(byteBuffer, reportData.TaskLocalID)
            packUUID(byteBuffer, reportData.TaskID)
            packFloat(byteBuffer, reportData.LocationX)
            packFloat(byteBuffer, reportData.LocationY)
            packFloat(byteBuffer, reportData.LocationZ)
            packFloat(byteBuffer, reportData.Score)
            packVariable(byteBuffer, reportData.TaskName, 1)
            packVariable(byteBuffer, reportData.OwnerName, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.RequestData_Field.ReportType = unpackInt(byteBuffer)
        this.RequestData_Field.RequestFlags = unpackInt(byteBuffer)
        this.RequestData_Field.TotalObjectCount = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ReportData reportData = ReportData()
            reportData.TaskLocalID = unpackInt(byteBuffer)
            reportData.TaskID = unpackUUID(byteBuffer)
            reportData.LocationX = unpackFloat(byteBuffer)
            reportData.LocationY = unpackFloat(byteBuffer)
            reportData.LocationZ = unpackFloat(byteBuffer)
            reportData.Score = unpackFloat(byteBuffer)
            reportData.TaskName = unpackVariable(byteBuffer, 1)
            reportData.OwnerName = unpackVariable(byteBuffer, 1)
            this.ReportData_Fields.add(reportData)
        }
    }
}
