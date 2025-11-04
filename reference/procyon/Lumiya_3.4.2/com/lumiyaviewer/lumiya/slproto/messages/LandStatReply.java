// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LandStatReply extends SLMessage
{
    public ArrayList<ReportData> ReportData_Fields;
    public RequestData RequestData_Field;
    
    public LandStatReply() {
        this.ReportData_Fields = new ArrayList<ReportData>();
        this.zeroCoded = false;
        this.RequestData_Field = new RequestData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ReportData_Fields.iterator();
        int n = 17;
        while (iterator.hasNext()) {
            final ReportData reportData = iterator.next();
            n += reportData.OwnerName.length + (reportData.TaskName.length + 37 + 1);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLandStatReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-90));
        this.packInt(byteBuffer, this.RequestData_Field.ReportType);
        this.packInt(byteBuffer, this.RequestData_Field.RequestFlags);
        this.packInt(byteBuffer, this.RequestData_Field.TotalObjectCount);
        byteBuffer.put((byte)this.ReportData_Fields.size());
        for (final ReportData reportData : this.ReportData_Fields) {
            this.packInt(byteBuffer, reportData.TaskLocalID);
            this.packUUID(byteBuffer, reportData.TaskID);
            this.packFloat(byteBuffer, reportData.LocationX);
            this.packFloat(byteBuffer, reportData.LocationY);
            this.packFloat(byteBuffer, reportData.LocationZ);
            this.packFloat(byteBuffer, reportData.Score);
            this.packVariable(byteBuffer, reportData.TaskName, 1);
            this.packVariable(byteBuffer, reportData.OwnerName, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RequestData_Field.ReportType = this.unpackInt(byteBuffer);
        this.RequestData_Field.RequestFlags = this.unpackInt(byteBuffer);
        this.RequestData_Field.TotalObjectCount = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ReportData e = new ReportData();
            e.TaskLocalID = this.unpackInt(byteBuffer);
            e.TaskID = this.unpackUUID(byteBuffer);
            e.LocationX = this.unpackFloat(byteBuffer);
            e.LocationY = this.unpackFloat(byteBuffer);
            e.LocationZ = this.unpackFloat(byteBuffer);
            e.Score = this.unpackFloat(byteBuffer);
            e.TaskName = this.unpackVariable(byteBuffer, 1);
            e.OwnerName = this.unpackVariable(byteBuffer, 1);
            this.ReportData_Fields.add(e);
        }
    }
    
    public static class ReportData
    {
        public float LocationX;
        public float LocationY;
        public float LocationZ;
        public byte[] OwnerName;
        public float Score;
        public UUID TaskID;
        public int TaskLocalID;
        public byte[] TaskName;
    }
    
    public static class RequestData
    {
        public int ReportType;
        public int RequestFlags;
        public int TotalObjectCount;
    }
}
