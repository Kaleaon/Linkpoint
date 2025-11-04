// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.net.Inet4Address;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimulatorPresentAtLocation extends SLMessage
{
    public NeighborBlock[] NeighborBlock_Fields;
    public SimulatorBlock SimulatorBlock_Field;
    public SimulatorPublicHostBlock SimulatorPublicHostBlock_Field;
    public ArrayList<TelehubBlock> TelehubBlock_Fields;
    
    public SimulatorPresentAtLocation() {
        int i = 0;
        this.NeighborBlock_Fields = new NeighborBlock[4];
        this.TelehubBlock_Fields = new ArrayList<TelehubBlock>();
        this.zeroCoded = false;
        this.SimulatorPublicHostBlock_Field = new SimulatorPublicHostBlock();
        while (i < 4) {
            this.NeighborBlock_Fields[i] = new NeighborBlock();
            ++i;
        }
        this.SimulatorBlock_Field = new SimulatorBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 42 + 1 + this.TelehubBlock_Fields.size() * 13;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimulatorPresentAtLocation(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        int i = 0;
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)11);
        this.packShort(byteBuffer, (short)this.SimulatorPublicHostBlock_Field.Port);
        this.packIPAddress(byteBuffer, this.SimulatorPublicHostBlock_Field.SimulatorIP);
        this.packInt(byteBuffer, this.SimulatorPublicHostBlock_Field.GridX);
        this.packInt(byteBuffer, this.SimulatorPublicHostBlock_Field.GridY);
        while (i < 4) {
            this.packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].IP);
            this.packShort(byteBuffer, (short)this.NeighborBlock_Fields[i].Port);
            ++i;
        }
        this.packVariable(byteBuffer, this.SimulatorBlock_Field.SimName, 1);
        this.packByte(byteBuffer, (byte)this.SimulatorBlock_Field.SimAccess);
        this.packInt(byteBuffer, this.SimulatorBlock_Field.RegionFlags);
        this.packUUID(byteBuffer, this.SimulatorBlock_Field.RegionID);
        this.packInt(byteBuffer, this.SimulatorBlock_Field.EstateID);
        this.packInt(byteBuffer, this.SimulatorBlock_Field.ParentEstateID);
        byteBuffer.put((byte)this.TelehubBlock_Fields.size());
        for (final TelehubBlock telehubBlock : this.TelehubBlock_Fields) {
            this.packBoolean(byteBuffer, telehubBlock.HasTelehub);
            this.packLLVector3(byteBuffer, telehubBlock.TelehubPos);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.SimulatorPublicHostBlock_Field.Port = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.SimulatorPublicHostBlock_Field.SimulatorIP = this.unpackIPAddress(byteBuffer);
        this.SimulatorPublicHostBlock_Field.GridX = this.unpackInt(byteBuffer);
        this.SimulatorPublicHostBlock_Field.GridY = this.unpackInt(byteBuffer);
        for (int i = 0; i < 4; ++i) {
            this.NeighborBlock_Fields[i].IP = this.unpackIPAddress(byteBuffer);
            this.NeighborBlock_Fields[i].Port = (this.unpackShort(byteBuffer) & 0xFFFF);
        }
        this.SimulatorBlock_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.SimulatorBlock_Field.SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
        this.SimulatorBlock_Field.RegionFlags = this.unpackInt(byteBuffer);
        this.SimulatorBlock_Field.RegionID = this.unpackUUID(byteBuffer);
        this.SimulatorBlock_Field.EstateID = this.unpackInt(byteBuffer);
        this.SimulatorBlock_Field.ParentEstateID = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int j = n; j < (value & 0xFF); ++j) {
            final TelehubBlock e = new TelehubBlock();
            e.HasTelehub = this.unpackBoolean(byteBuffer);
            e.TelehubPos = this.unpackLLVector3(byteBuffer);
            this.TelehubBlock_Fields.add(e);
        }
    }
    
    public static class NeighborBlock
    {
        public Inet4Address IP;
        public int Port;
    }
    
    public static class SimulatorBlock
    {
        public int EstateID;
        public int ParentEstateID;
        public int RegionFlags;
        public UUID RegionID;
        public int SimAccess;
        public byte[] SimName;
    }
    
    public static class SimulatorPublicHostBlock
    {
        public int GridX;
        public int GridY;
        public int Port;
        public Inet4Address SimulatorIP;
    }
    
    public static class TelehubBlock
    {
        public boolean HasTelehub;
        public LLVector3 TelehubPos;
    }
}
