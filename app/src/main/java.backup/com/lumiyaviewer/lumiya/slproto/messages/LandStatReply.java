package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class LandStatReply extends SLMessage {
    public ArrayList<ReportData> ReportData_Fields = new ArrayList<>();
    public RequestData RequestData_Field;

    public static class ReportData {
        public float LocationX;
        public float LocationY;
        public float LocationZ;
        public byte[] OwnerName;
        public float Score;
        public UUID TaskID;
        public int TaskLocalID;
        public byte[] TaskName;
    }

    public static class RequestData {
        public int ReportType;
        public int RequestFlags;
        public int TotalObjectCount;
    }

    public LandStatReply() {
        this.zeroCoded = false;
        this.RequestData_Field = new RequestData();
    }

    public int CalcPayloadSize() {
        int i = 17;
        Iterator<T> it = this.ReportData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            ReportData reportData = (ReportData) it.next();
            i = reportData.OwnerName.length + reportData.TaskName.length + 37 + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLandStatReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -90);
        packInt(byteBuffer, this.RequestData_Field.ReportType);
        packInt(byteBuffer, this.RequestData_Field.RequestFlags);
        packInt(byteBuffer, this.RequestData_Field.TotalObjectCount);
        byteBuffer.put((byte) this.ReportData_Fields.size());
        for (ReportData reportData : this.ReportData_Fields) {
            packInt(byteBuffer, reportData.TaskLocalID);
            packUUID(byteBuffer, reportData.TaskID);
            packFloat(byteBuffer, reportData.LocationX);
            packFloat(byteBuffer, reportData.LocationY);
            packFloat(byteBuffer, reportData.LocationZ);
            packFloat(byteBuffer, reportData.Score);
            packVariable(byteBuffer, reportData.TaskName, 1);
            packVariable(byteBuffer, reportData.OwnerName, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestData_Field.ReportType = unpackInt(byteBuffer);
        this.RequestData_Field.RequestFlags = unpackInt(byteBuffer);
        this.RequestData_Field.TotalObjectCount = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ReportData reportData = new ReportData();
            reportData.TaskLocalID = unpackInt(byteBuffer);
            reportData.TaskID = unpackUUID(byteBuffer);
            reportData.LocationX = unpackFloat(byteBuffer);
            reportData.LocationY = unpackFloat(byteBuffer);
            reportData.LocationZ = unpackFloat(byteBuffer);
            reportData.Score = unpackFloat(byteBuffer);
            reportData.TaskName = unpackVariable(byteBuffer, 1);
            reportData.OwnerName = unpackVariable(byteBuffer, 1);
            this.ReportData_Fields.add(reportData);
        }
    }
}
