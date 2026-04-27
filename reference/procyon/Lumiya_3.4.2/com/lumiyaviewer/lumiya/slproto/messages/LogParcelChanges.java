// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LogParcelChanges extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<ParcelData> ParcelData_Fields;
    public RegionData RegionData_Field;
    
    public LogParcelChanges() {
        this.ParcelData_Fields = new ArrayList<ParcelData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RegionData_Field = new RegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Fields.size() * 54 + 29;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLogParcelChanges(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-32));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        byteBuffer.put((byte)this.ParcelData_Fields.size());
        for (final ParcelData parcelData : this.ParcelData_Fields) {
            this.packUUID(byteBuffer, parcelData.ParcelID);
            this.packUUID(byteBuffer, parcelData.OwnerID);
            this.packBoolean(byteBuffer, parcelData.IsOwnerGroup);
            this.packInt(byteBuffer, parcelData.ActualArea);
            this.packByte(byteBuffer, (byte)parcelData.Action);
            this.packUUID(byteBuffer, parcelData.TransactionID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.RegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ParcelData e = new ParcelData();
            e.ParcelID = this.unpackUUID(byteBuffer);
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.IsOwnerGroup = this.unpackBoolean(byteBuffer);
            e.ActualArea = this.unpackInt(byteBuffer);
            e.Action = this.unpackByte(byteBuffer);
            e.TransactionID = this.unpackUUID(byteBuffer);
            this.ParcelData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class ParcelData
    {
        public int Action;
        public int ActualArea;
        public boolean IsOwnerGroup;
        public UUID OwnerID;
        public UUID ParcelID;
        public UUID TransactionID;
    }
    
    public static class RegionData
    {
        public long RegionHandle;
    }
}
