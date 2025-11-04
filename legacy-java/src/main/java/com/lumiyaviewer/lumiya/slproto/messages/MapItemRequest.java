package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class MapItemRequest extends SLMessage {
    public AgentData AgentData_Field;
    public RequestData RequestData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class RequestData {
        public int ItemType;
        public long RegionHandle;
    }

    public MapItemRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.RequestData_Field = new RequestData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 57;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapItemRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -102);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.Flags);
        packInt(byteBuffer, this.AgentData_Field.EstateID);
        packBoolean(byteBuffer, this.AgentData_Field.Godlike);
        packInt(byteBuffer, this.RequestData_Field.ItemType);
        packLong(byteBuffer, this.RequestData_Field.RegionHandle);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = unpackInt(byteBuffer);
        this.AgentData_Field.EstateID = unpackInt(byteBuffer);
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer);
        this.RequestData_Field.ItemType = unpackInt(byteBuffer);
        this.RequestData_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
