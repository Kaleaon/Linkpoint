package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class FindAgent extends SLMessage {
    public AgentBlock AgentBlock_Field;
    public ArrayList<LocationBlock> LocationBlock_Fields = new ArrayList<>();

    public static class AgentBlock {
        public UUID Hunter;
        public UUID Prey;
        public Inet4Address SpaceIP;
    }

    public static class LocationBlock {
        public double GlobalX;
        public double GlobalY;
    }

    public FindAgent() {
        this.zeroCoded = false;
        this.AgentBlock_Field = new AgentBlock();
    }

    public int CalcPayloadSize() {
        return (this.LocationBlock_Fields.size() * 16) + 41;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFindAgent(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
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

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentBlock_Field.Hunter = unpackUUID(byteBuffer);
        this.AgentBlock_Field.Prey = unpackUUID(byteBuffer);
        this.AgentBlock_Field.SpaceIP = unpackIPAddress(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            LocationBlock locationBlock = new LocationBlock();
            locationBlock.GlobalX = unpackDouble(byteBuffer);
            locationBlock.GlobalY = unpackDouble(byteBuffer);
            this.LocationBlock_Fields.add(locationBlock);
        }
    }
}
