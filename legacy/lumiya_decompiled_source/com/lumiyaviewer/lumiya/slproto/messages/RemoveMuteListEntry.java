package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class RemoveMuteListEntry extends SLMessage {
    public AgentData AgentData_Field;
    public MuteData MuteData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class MuteData {
        public UUID MuteID;
        public byte[] MuteName;
    }

    public RemoveMuteListEntry() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.MuteData_Field = new MuteData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.MuteData_Field.MuteName.length + 17 + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveMuteListEntry(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 8);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.MuteData_Field.MuteID);
        packVariable(byteBuffer, this.MuteData_Field.MuteName, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.MuteData_Field.MuteID = unpackUUID(byteBuffer);
        this.MuteData_Field.MuteName = unpackVariable(byteBuffer, 1);
    }
}
