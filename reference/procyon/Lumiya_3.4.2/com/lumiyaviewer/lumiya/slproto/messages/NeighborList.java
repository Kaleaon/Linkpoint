// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class NeighborList extends SLMessage
{
    public NeighborBlock[] NeighborBlock_Fields;
    
    public NeighborList() {
        int i = 0;
        this.NeighborBlock_Fields = new NeighborBlock[4];
        this.zeroCoded = false;
        while (i < 4) {
            this.NeighborBlock_Fields[i] = new NeighborBlock();
            ++i;
        }
    }
    
    @Override
    public int CalcPayloadSize() {
        int n = 1;
        for (int i = 0; i < 4; ++i) {
            n += this.NeighborBlock_Fields[i].Name.length + 29 + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleNeighborList(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)3);
        for (int i = 0; i < 4; ++i) {
            this.packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].IP);
            this.packShort(byteBuffer, (short)this.NeighborBlock_Fields[i].Port);
            this.packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].PublicIP);
            this.packShort(byteBuffer, (short)this.NeighborBlock_Fields[i].PublicPort);
            this.packUUID(byteBuffer, this.NeighborBlock_Fields[i].RegionID);
            this.packVariable(byteBuffer, this.NeighborBlock_Fields[i].Name, 1);
            this.packByte(byteBuffer, (byte)this.NeighborBlock_Fields[i].SimAccess);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        for (int i = 0; i < 4; ++i) {
            this.NeighborBlock_Fields[i].IP = this.unpackIPAddress(byteBuffer);
            this.NeighborBlock_Fields[i].Port = (this.unpackShort(byteBuffer) & 0xFFFF);
            this.NeighborBlock_Fields[i].PublicIP = this.unpackIPAddress(byteBuffer);
            this.NeighborBlock_Fields[i].PublicPort = (this.unpackShort(byteBuffer) & 0xFFFF);
            this.NeighborBlock_Fields[i].RegionID = this.unpackUUID(byteBuffer);
            this.NeighborBlock_Fields[i].Name = this.unpackVariable(byteBuffer, 1);
            this.NeighborBlock_Fields[i].SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
        }
    }
    
    public static class NeighborBlock
    {
        public Inet4Address IP;
        public byte[] Name;
        public int Port;
        public Inet4Address PublicIP;
        public int PublicPort;
        public UUID RegionID;
        public int SimAccess;
    }
}
