package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DataHomeLocationRequest extends SLMessage {
    public AgentInfo AgentInfo_Field = new AgentInfo();
    public Info Info_Field = new Info();

    public static class AgentInfo {
        public int AgentEffectiveMaturity;
    }

    public static class Info {
        public UUID AgentID;
        public int KickedFromEstateID;
    }

    public DataHomeLocationRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 28;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataHomeLocationRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 67);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packInt(byteBuffer, this.Info_Field.KickedFromEstateID);
        packInt(byteBuffer, this.AgentInfo_Field.AgentEffectiveMaturity);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.KickedFromEstateID = unpackInt(byteBuffer);
        this.AgentInfo_Field.AgentEffectiveMaturity = unpackInt(byteBuffer);
    }
}
