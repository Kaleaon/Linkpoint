package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

/* loaded from: classes.dex */
public class FindAgent extends SLMessage {
    public AgentBlock AgentBlock_Field;
    public ArrayList<LocationBlock> LocationBlock_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentBlock {
        public UUID Hunter;
        public UUID Prey;
        public Inet4Address SpaceIP;
    }

    /* loaded from: classes.dex */
    public static class LocationBlock {
        public double GlobalX;
        public double GlobalY;
    }

    public FindAgent() {
        this.zeroCoded = false;
        this.AgentBlock_Field = new AgentBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.LocationBlock_Fields.size() * 16) + 41;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFindAgent(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 0);
        packUUID(byteBuffer, this.AgentBlock_Field.Hunter);
        packUUID(byteBuffer, this.AgentBlock_Field.Prey);
        packIPAddress(byteBuffer, this.AgentBlock_Field.SpaceIP);
        byteBuffer.put((byte) this.LocationBlock_Fields.size());
        for (LocationBlock locationBlock : this.LocationBlock_Fields) {
            packDouble(byteBuffer, locationBlock.GlobalX);
            packDouble(byteBuffer, locationBlock.GlobalY);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentBlock_Field.Hunter = unpackUUID(byteBuffer);
        this.AgentBlock_Field.Prey = unpackUUID(byteBuffer);
        this.AgentBlock_Field.SpaceIP = unpackIPAddress(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            LocationBlock locationBlock = new LocationBlock();
            locationBlock.GlobalX = unpackDouble(byteBuffer);
            locationBlock.GlobalY = unpackDouble(byteBuffer);
            this.LocationBlock_Fields.add(locationBlock);
        }
    }
}
