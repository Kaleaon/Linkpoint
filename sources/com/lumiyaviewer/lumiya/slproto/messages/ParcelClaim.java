package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ParcelClaim extends SLMessage {
    public AgentData AgentData_Field;
    public Data Data_Field;
    public ArrayList<ParcelData> ParcelData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public boolean Final;
        public UUID GroupID;
        public boolean IsGroupOwned;
    }

    public static class ParcelData {
        public float East;
        public float North;
        public float South;
        public float West;
    }

    public ParcelClaim() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }

    public int CalcPayloadSize() {
        return (this.ParcelData_Fields.size() * 16) + 55;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelClaim(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -47);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.GroupID);
        packBoolean(byteBuffer, this.Data_Field.IsGroupOwned);
        packBoolean(byteBuffer, this.Data_Field.Final);
        byteBuffer.put((byte) this.ParcelData_Fields.size());
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packFloat(byteBuffer, parcelData.West);
            packFloat(byteBuffer, parcelData.South);
            packFloat(byteBuffer, parcelData.East);
            packFloat(byteBuffer, parcelData.North);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.GroupID = unpackUUID(byteBuffer);
        this.Data_Field.IsGroupOwned = unpackBoolean(byteBuffer);
        this.Data_Field.Final = unpackBoolean(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ParcelData parcelData = new ParcelData();
            parcelData.West = unpackFloat(byteBuffer);
            parcelData.South = unpackFloat(byteBuffer);
            parcelData.East = unpackFloat(byteBuffer);
            parcelData.North = unpackFloat(byteBuffer);
            this.ParcelData_Fields.add(parcelData);
        }
    }
}
