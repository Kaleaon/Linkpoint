package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

/* loaded from: classes.dex */
public class SimulatorPresentAtLocation extends SLMessage {
    public SimulatorBlock SimulatorBlock_Field;
    public SimulatorPublicHostBlock SimulatorPublicHostBlock_Field;
    public NeighborBlock[] NeighborBlock_Fields = new NeighborBlock[4];
    public ArrayList<TelehubBlock> TelehubBlock_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class NeighborBlock {
        public Inet4Address IP;
        public int Port;
    }

    /* loaded from: classes.dex */
    public static class SimulatorBlock {
        public int EstateID;
        public int ParentEstateID;
        public int RegionFlags;
        public UUID RegionID;
        public int SimAccess;
        public byte[] SimName;
    }

    /* loaded from: classes.dex */
    public static class SimulatorPublicHostBlock {
        public int GridX;
        public int GridY;
        public int Port;
        public Inet4Address SimulatorIP;
    }

    /* loaded from: classes.dex */
    public static class TelehubBlock {
        public boolean HasTelehub;
        public LLVector3 TelehubPos;
    }

    public SimulatorPresentAtLocation() {
        this.zeroCoded = false;
        this.SimulatorPublicHostBlock_Field = new SimulatorPublicHostBlock();
        for (int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i] = new NeighborBlock();
        }
        this.SimulatorBlock_Field = new SimulatorBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 42 + 1 + (this.TelehubBlock_Fields.size() * 13);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorPresentAtLocation(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.VT);
        packShort(byteBuffer, (short) this.SimulatorPublicHostBlock_Field.Port);
        packIPAddress(byteBuffer, this.SimulatorPublicHostBlock_Field.SimulatorIP);
        packInt(byteBuffer, this.SimulatorPublicHostBlock_Field.GridX);
        packInt(byteBuffer, this.SimulatorPublicHostBlock_Field.GridY);
        for (int i = 0; i < 4; i++) {
            packIPAddress(byteBuffer, this.NeighborBlock_Fields[i].IP);
            packShort(byteBuffer, (short) this.NeighborBlock_Fields[i].Port);
        }
        packVariable(byteBuffer, this.SimulatorBlock_Field.SimName, 1);
        packByte(byteBuffer, (byte) this.SimulatorBlock_Field.SimAccess);
        packInt(byteBuffer, this.SimulatorBlock_Field.RegionFlags);
        packUUID(byteBuffer, this.SimulatorBlock_Field.RegionID);
        packInt(byteBuffer, this.SimulatorBlock_Field.EstateID);
        packInt(byteBuffer, this.SimulatorBlock_Field.ParentEstateID);
        byteBuffer.put((byte) this.TelehubBlock_Fields.size());
        for (TelehubBlock telehubBlock : this.TelehubBlock_Fields) {
            packBoolean(byteBuffer, telehubBlock.HasTelehub);
            packLLVector3(byteBuffer, telehubBlock.TelehubPos);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorPublicHostBlock_Field.Port = unpackShort(byteBuffer) & 65535;
        this.SimulatorPublicHostBlock_Field.SimulatorIP = unpackIPAddress(byteBuffer);
        this.SimulatorPublicHostBlock_Field.GridX = unpackInt(byteBuffer);
        this.SimulatorPublicHostBlock_Field.GridY = unpackInt(byteBuffer);
        for (int i = 0; i < 4; i++) {
            this.NeighborBlock_Fields[i].IP = unpackIPAddress(byteBuffer);
            this.NeighborBlock_Fields[i].Port = unpackShort(byteBuffer) & 65535;
        }
        this.SimulatorBlock_Field.SimName = unpackVariable(byteBuffer, 1);
        this.SimulatorBlock_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.SimulatorBlock_Field.RegionFlags = unpackInt(byteBuffer);
        this.SimulatorBlock_Field.RegionID = unpackUUID(byteBuffer);
        this.SimulatorBlock_Field.EstateID = unpackInt(byteBuffer);
        this.SimulatorBlock_Field.ParentEstateID = unpackInt(byteBuffer);
        int i2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i3 = 0; i3 < i2; i3++) {
            TelehubBlock telehubBlock = new TelehubBlock();
            telehubBlock.HasTelehub = unpackBoolean(byteBuffer);
            telehubBlock.TelehubPos = unpackLLVector3(byteBuffer);
            this.TelehubBlock_Fields.add(telehubBlock);
        }
    }
}
