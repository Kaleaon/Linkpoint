package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class ParcelDwellReply extends SLMessage {
    public AgentData AgentData_Field;
    public Data Data_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class Data {
        public float Dwell;
        public int LocalID;
        public UUID ParcelID;
    }

    public ParcelDwellReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 44;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelDwellReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -37);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packInt(byteBuffer, this.Data_Field.LocalID);
        packUUID(byteBuffer, this.Data_Field.ParcelID);
        packFloat(byteBuffer, this.Data_Field.Dwell);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.Data_Field.LocalID = unpackInt(byteBuffer);
        this.Data_Field.ParcelID = unpackUUID(byteBuffer);
        this.Data_Field.Dwell = unpackFloat(byteBuffer);
    }
}
