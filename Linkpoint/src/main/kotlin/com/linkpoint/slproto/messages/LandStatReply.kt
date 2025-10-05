package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class LandStatReply : SLMessage() {
    public ArrayList<ReportData> ReportData_Fields = ArrayList<>()
    public RequestData RequestData_Field

    @JvmStatic
    class ReportData {
        public Float LocationX
        public Float LocationY
        public Float LocationZ
        public Byte[] OwnerName
        public Float Score
        public UUID TaskID
        public Int TaskLocalID
        public Byte[] TaskName
    }

    @JvmStatic
    class RequestData {
        public Int ReportType
        public Int RequestFlags
        public Int TotalObjectCount
    }

    public LandStatReply() {
        this.zeroCoded = false
        this.RequestData_Field = RequestData()
    }

    public Int CalcPayloadSize() {
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

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLandStatReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -90)
        packInt(byteBuffer, this.RequestData_Field.ReportType)
        packInt(byteBuffer, this.RequestData_Field.RequestFlags)
        packInt(byteBuffer, this.RequestData_Field.TotalObjectCount)
        byteBuffer.put((Byte) this.ReportData_Fields.size())
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestData_Field.ReportType = unpackInt(byteBuffer)
        this.RequestData_Field.RequestFlags = unpackInt(byteBuffer)
        this.RequestData_Field.TotalObjectCount = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
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
