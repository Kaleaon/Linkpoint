package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ParcelAccessListUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public Data Data_Field;
    public ArrayList<List> List_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public int Flags;
        public int LocalID;
        public int Sections;
        public int SequenceID;
        public UUID TransactionID;
    }

    public static class List {
        public int Flags;
        public UUID ID;
        public int Time;
    }

    public ParcelAccessListUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }

    public int CalcPayloadSize() {
        return (this.List_Fields.size() * 24) + 69;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelAccessListUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -39);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.Data_Field.Flags);
        packInt(byteBuffer, this.Data_Field.LocalID);
        packUUID(byteBuffer, this.Data_Field.TransactionID);
        packInt(byteBuffer, this.Data_Field.SequenceID);
        packInt(byteBuffer, this.Data_Field.Sections);
        byteBuffer.put((byte) this.List_Fields.size());
        for (List list : this.List_Fields) {
            packUUID(byteBuffer, list.ID);
            packInt(byteBuffer, list.Time);
            packInt(byteBuffer, list.Flags);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.Flags = unpackInt(byteBuffer);
        this.Data_Field.LocalID = unpackInt(byteBuffer);
        this.Data_Field.TransactionID = unpackUUID(byteBuffer);
        this.Data_Field.SequenceID = unpackInt(byteBuffer);
        this.Data_Field.Sections = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            List list = new List();
            list.ID = unpackUUID(byteBuffer);
            list.Time = unpackInt(byteBuffer);
            list.Flags = unpackInt(byteBuffer);
            this.List_Fields.add(list);
        }
    }
}
