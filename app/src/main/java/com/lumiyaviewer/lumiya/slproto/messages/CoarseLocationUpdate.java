package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class CoarseLocationUpdate extends SLMessage {
    public ArrayList<AgentData> AgentData_Fields = new ArrayList<>();
    public Index Index_Field;
    public ArrayList<Location> Location_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class Index {
        public int Prey;
        public int You;
    }

    public static class Location {
        public int X;
        public int Y;
        public int Z;
    }

    public CoarseLocationUpdate() {
        this.zeroCoded = false;
        this.Index_Field = new Index();
    }

    public int CalcPayloadSize() {
        return (this.Location_Fields.size() * 3) + 3 + 4 + 1 + (this.AgentData_Fields.size() * 16);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCoarseLocationUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 6);
        byteBuffer.put((byte) this.Location_Fields.size());
        for (Location location : this.Location_Fields) {
            packByte(byteBuffer, (byte) location.X);
            packByte(byteBuffer, (byte) location.Y);
            packByte(byteBuffer, (byte) location.Z);
        }
        packShort(byteBuffer, (short) this.Index_Field.You);
        packShort(byteBuffer, (short) this.Index_Field.Prey);
        byteBuffer.put((byte) this.AgentData_Fields.size());
        for (AgentData agentData : this.AgentData_Fields) {
            packUUID(byteBuffer, agentData.AgentID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Location location = new Location();
            location.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            location.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            location.Z = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.Location_Fields.add(location);
        }
        this.Index_Field.You = unpackShort(byteBuffer);
        this.Index_Field.Prey = unpackShort(byteBuffer);
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            AgentData agentData = new AgentData();
            agentData.AgentID = unpackUUID(byteBuffer);
            this.AgentData_Fields.add(agentData);
        }
    }
}
