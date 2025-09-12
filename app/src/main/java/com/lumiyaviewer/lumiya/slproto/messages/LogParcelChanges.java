package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class LogParcelChanges extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<ParcelData> ParcelData_Fields = new ArrayList<>();
    public RegionData RegionData_Field;

    public static class AgentData {
        public UUID AgentID;
    }

    public static class ParcelData {
        public int Action;
        public int ActualArea;
        public boolean IsOwnerGroup;
        public UUID OwnerID;
        public UUID ParcelID;
        public UUID TransactionID;
    }

    public static class RegionData {
        public long RegionHandle;
    }

    public LogParcelChanges() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize() {
        return (this.ParcelData_Fields.size() * 54) + 29;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogParcelChanges(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -32);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        byteBuffer.put((byte) this.ParcelData_Fields.size());
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID);
            packUUID(byteBuffer, parcelData.OwnerID);
            packBoolean(byteBuffer, parcelData.IsOwnerGroup);
            packInt(byteBuffer, parcelData.ActualArea);
            packByte(byteBuffer, (byte) parcelData.Action);
            packUUID(byteBuffer, parcelData.TransactionID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ParcelData parcelData = new ParcelData();
            parcelData.ParcelID = unpackUUID(byteBuffer);
            parcelData.OwnerID = unpackUUID(byteBuffer);
            parcelData.IsOwnerGroup = unpackBoolean(byteBuffer);
            parcelData.ActualArea = unpackInt(byteBuffer);
            parcelData.Action = unpackByte(byteBuffer);
            parcelData.TransactionID = unpackUUID(byteBuffer);
            this.ParcelData_Fields.add(parcelData);
        }
    }
}
