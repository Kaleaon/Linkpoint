package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class CoarseLocationUpdate extends SLMessage {
    public Index Index_Field;
    public ArrayList<Location> Location_Fields = new ArrayList<>();
    public ArrayList<AgentData> AgentData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class Index {
        public int Prey;
        public int You;
    }

    /* loaded from: classes.dex */
    public static class Location {
        public int X;
        public int Y;
        public int Z;
    }

    public CoarseLocationUpdate() {
        this.zeroCoded = false;
        this.Index_Field = new Index();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.Location_Fields.size() * 3) + 3 + 4 + 1 + (this.AgentData_Fields.size() * 16);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCoarseLocationUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
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
        Iterator<T> it = this.AgentData_Fields.iterator();
        while (it.hasNext()) {
            packUUID(byteBuffer, ((AgentData) it.next()).AgentID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            Location location = new Location();
            location.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            location.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            location.Z = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.Location_Fields.add(location);
        }
        this.Index_Field.You = unpackShort(byteBuffer);
        this.Index_Field.Prey = unpackShort(byteBuffer);
        int i3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i4 = 0; i4 < i3; i4++) {
            AgentData agentData = new AgentData();
            agentData.AgentID = unpackUUID(byteBuffer);
            this.AgentData_Fields.add(agentData);
        }
    }
}
