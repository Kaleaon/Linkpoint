package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class DeRezObject extends SLMessage {
    public AgentBlock AgentBlock_Field;
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class AgentBlock {
        public int Destination;
        public UUID DestinationID;
        public UUID GroupID;
        public int PacketCount;
        public int PacketNumber;
        public UUID TransactionID;
    }

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public int ObjectLocalID;
    }

    public DeRezObject() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.AgentBlock_Field = new AgentBlock();
    }

    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 88;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDeRezObject(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 35);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentBlock_Field.GroupID);
        packByte(byteBuffer, (byte) this.AgentBlock_Field.Destination);
        packUUID(byteBuffer, this.AgentBlock_Field.DestinationID);
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID);
        packByte(byteBuffer, (byte) this.AgentBlock_Field.PacketCount);
        packByte(byteBuffer, (byte) this.AgentBlock_Field.PacketNumber);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.GroupID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.Destination = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentBlock_Field.DestinationID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.PacketCount = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentBlock_Field.PacketNumber = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectLocalID = unpackInt(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
