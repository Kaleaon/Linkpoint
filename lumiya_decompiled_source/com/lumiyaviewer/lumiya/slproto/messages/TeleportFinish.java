package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class TeleportFinish extends SLMessage {
    public Info Info_Field;

    /* loaded from: classes.dex */
    public static class Info {
        public UUID AgentID;
        public int LocationID;
        public long RegionHandle;
        public byte[] SeedCapability;
        public int SimAccess;
        public Inet4Address SimIP;
        public int SimPort;
        public int TeleportFlags;
    }

    public TeleportFinish() {
        this.zeroCoded = false;
        this.Info_Field = new Info();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.Info_Field.SeedCapability.length + 36 + 1 + 4 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportFinish(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 69);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packInt(byteBuffer, this.Info_Field.LocationID);
        packIPAddress(byteBuffer, this.Info_Field.SimIP);
        packShort(byteBuffer, (short) this.Info_Field.SimPort);
        packLong(byteBuffer, this.Info_Field.RegionHandle);
        packVariable(byteBuffer, this.Info_Field.SeedCapability, 2);
        packByte(byteBuffer, (byte) this.Info_Field.SimAccess);
        packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.LocationID = unpackInt(byteBuffer);
        this.Info_Field.SimIP = unpackIPAddress(byteBuffer);
        this.Info_Field.SimPort = unpackShort(byteBuffer) & 65535;
        this.Info_Field.RegionHandle = unpackLong(byteBuffer);
        this.Info_Field.SeedCapability = unpackVariable(byteBuffer, 2);
        this.Info_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer);
    }
}
