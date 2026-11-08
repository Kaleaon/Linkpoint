package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class DataHomeLocationRequest extends SLMessage {
    public AgentInfo AgentInfo_Field;
    public Info Info_Field;

    /* loaded from: classes.dex */
    public static class AgentInfo {
        public int AgentEffectiveMaturity;
    }

    /* loaded from: classes.dex */
    public static class Info {
        public UUID AgentID;
        public int KickedFromEstateID;
    }

    public DataHomeLocationRequest() {
        this.zeroCoded = true;
        this.Info_Field = new Info();
        this.AgentInfo_Field = new AgentInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 28;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataHomeLocationRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 67);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packInt(byteBuffer, this.Info_Field.KickedFromEstateID);
        packInt(byteBuffer, this.AgentInfo_Field.AgentEffectiveMaturity);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.KickedFromEstateID = unpackInt(byteBuffer);
        this.AgentInfo_Field.AgentEffectiveMaturity = unpackInt(byteBuffer);
    }
}
