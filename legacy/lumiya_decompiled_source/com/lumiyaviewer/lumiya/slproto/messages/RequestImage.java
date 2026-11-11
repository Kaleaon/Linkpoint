package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class RequestImage extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<RequestImageData> RequestImageData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class RequestImageData {
        public int DiscardLevel;
        public float DownloadPriority;
        public UUID Image;
        public int Packet;
        public int Type;
    }

    public RequestImage() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.RequestImageData_Fields.size() * 26) + 34;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestImage(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 8);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte) this.RequestImageData_Fields.size());
        for (RequestImageData requestImageData : this.RequestImageData_Fields) {
            packUUID(byteBuffer, requestImageData.Image);
            packByte(byteBuffer, (byte) requestImageData.DiscardLevel);
            packFloat(byteBuffer, requestImageData.DownloadPriority);
            packInt(byteBuffer, requestImageData.Packet);
            packByte(byteBuffer, (byte) requestImageData.Type);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            RequestImageData requestImageData = new RequestImageData();
            requestImageData.Image = unpackUUID(byteBuffer);
            requestImageData.DiscardLevel = unpackByte(byteBuffer);
            requestImageData.DownloadPriority = unpackFloat(byteBuffer);
            requestImageData.Packet = unpackInt(byteBuffer);
            requestImageData.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.RequestImageData_Fields.add(requestImageData);
        }
    }
}
