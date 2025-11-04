package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class DeRezObject extends SLMessage {
    public AgentBlock AgentBlock_Field;
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentBlock {
        public int Destination;
        public UUID DestinationID;
        public UUID GroupID;
        public int PacketCount;
        public int PacketNumber;
        public UUID TransactionID;
    }

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class ObjectData {
        public int ObjectLocalID;
    }

    public DeRezObject() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.AgentBlock_Field = new AgentBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 88;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDeRezObject(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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
        Iterator<T> it = this.ObjectData_Fields.iterator();
        while (it.hasNext()) {
            packInt(byteBuffer, ((ObjectData) it.next()).ObjectLocalID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.GroupID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.Destination = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentBlock_Field.DestinationID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.PacketCount = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentBlock_Field.PacketNumber = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectLocalID = unpackInt(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
