package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class InitiateDownload extends SLMessage {
    public AgentData AgentData_Field;
    public FileData FileData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class FileData {
        public byte[] SimFilename;
        public byte[] ViewerFilename;
    }

    public InitiateDownload() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.FileData_Field = new FileData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.FileData_Field.SimFilename.length + 1 + 1 + this.FileData_Field.ViewerFilename.length + 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInitiateDownload(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -109);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packVariable(byteBuffer, this.FileData_Field.SimFilename, 1);
        packVariable(byteBuffer, this.FileData_Field.ViewerFilename, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.FileData_Field.SimFilename = unpackVariable(byteBuffer, 1);
        this.FileData_Field.ViewerFilename = unpackVariable(byteBuffer, 1);
    }
}
