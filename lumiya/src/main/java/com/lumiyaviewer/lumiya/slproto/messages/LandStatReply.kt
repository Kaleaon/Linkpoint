package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        byte[] OwnerName
        float Score
        UUID TaskID
        Int TaskLocalID
        byte[] TaskName
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

    Int CalcPayloadSize() {
        Int i = 17
        Iterator<T> it = this.ReportData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            ReportData reportData = (ReportData) it.next()
            i = reportData.OwnerName.length + reportData.TaskName.length + 37 + 1 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLandStatReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -90)
        packInt(byteBuffer, this.RequestData_Field.ReportType)
        packInt(byteBuffer, this.RequestData_Field.RequestFlags)
        packInt(byteBuffer, this.RequestData_Field.TotalObjectCount)
        byteBuffer.put((byte) this.ReportData_Fields.size())
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestData_Field.ReportType = unpackInt(byteBuffer)
        this.RequestData_Field.RequestFlags = unpackInt(byteBuffer)
        this.RequestData_Field.TotalObjectCount = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
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
