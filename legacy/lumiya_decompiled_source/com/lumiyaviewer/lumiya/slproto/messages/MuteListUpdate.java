package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class MuteListUpdate extends SLMessage {
    public MuteData MuteData_Field;

    /* loaded from: classes.dex */
    public static class MuteData {
        public UUID AgentID;
        public byte[] Filename;
    }

    public MuteListUpdate() {
        this.zeroCoded = false;
        this.MuteData_Field = new MuteData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.MuteData_Field.Filename.length + 17 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMuteListUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 62);
        packUUID(byteBuffer, this.MuteData_Field.AgentID);
        packVariable(byteBuffer, this.MuteData_Field.Filename, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MuteData_Field.AgentID = unpackUUID(byteBuffer);
        this.MuteData_Field.Filename = unpackVariable(byteBuffer, 1);
    }
}
