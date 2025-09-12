package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

public class NeighborList extends SLMessage {
    public NeighborBlock[] NeighborBlock_Fields = new NeighborBlock[4];

    public static class NeighborBlock {
        public Inet4Address IP;
        public byte[] Name;
        public int Port;
        public Inet4Address PublicIP;
        public int PublicPort;
        public UUID RegionID;
        public int SimAccess;
    }

    public NeighborList() {
        this.zeroCoded = false;
        for (int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i] = new NeighborBlock();
        }
    }

    public int CalcPayloadSize() {
        int i = 1;
        for (int i2 = 0; i2 < 4; i2++) {
            i += this.NeighborBlock_Fields[i2].Name.length + 29 + 1;
        }
        return i;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNeighborList(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 3);
        for (int i = 0; i < 4; i++) {
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].IP);
            packShort(byteBuffer, (short) this.NeighborBlock_Fields[i].Port);
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].PublicIP);
            packShort(byteBuffer, (short) this.NeighborBlock_Fields[i].PublicPort);
            packUUID(byteBuffer, this.NeighborBlock_Fields[i].RegionID);
            packVariable(byteBuffer, this.NeighborBlock_Fields[i].Name, 1);
            packByte(byteBuffer, (byte) this.NeighborBlock_Fields[i].SimAccess);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        for (int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i].IP = unpackIPAddress(byteBuffer);
            this.NeighborBlock_Fields[i].Port = unpackShort(byteBuffer) & 65535;
            this.NeighborBlock_Fields[i].PublicIP = unpackIPAddress(byteBuffer);
            this.NeighborBlock_Fields[i].PublicPort = unpackShort(byteBuffer) & 65535;
            this.NeighborBlock_Fields[i].RegionID = unpackUUID(byteBuffer);
            this.NeighborBlock_Fields[i].Name = unpackVariable(byteBuffer, 1);
            this.NeighborBlock_Fields[i].SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        }
    }
}
