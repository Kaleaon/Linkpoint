package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class ObjectGrab extends SLMessage {
    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    public ArrayList<SurfaceInfo> SurfaceInfo_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class ObjectData {
        public LLVector3 GrabOffset;
        public int LocalID;
    }

    /* loaded from: classes.dex */
    public static class SurfaceInfo {
        public LLVector3 Binormal;
        public int FaceIndex;
        public LLVector3 Normal;
        public LLVector3 Position;
        public LLVector3 STCoord;
        public LLVector3 UVCoord;
    }

    public ObjectGrab() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ObjectData_Field = new ObjectData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.SurfaceInfo_Fields.size() * 64) + 53;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectGrab(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 117);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ObjectData_Field.LocalID);
        packLLVector3(byteBuffer, this.ObjectData_Field.GrabOffset);
        byteBuffer.put((byte) this.SurfaceInfo_Fields.size());
        for (SurfaceInfo surfaceInfo : this.SurfaceInfo_Fields) {
            packLLVector3(byteBuffer, surfaceInfo.UVCoord);
            packLLVector3(byteBuffer, surfaceInfo.STCoord);
            packInt(byteBuffer, surfaceInfo.FaceIndex);
            packLLVector3(byteBuffer, surfaceInfo.Position);
            packLLVector3(byteBuffer, surfaceInfo.Normal);
            packLLVector3(byteBuffer, surfaceInfo.Binormal);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ObjectData_Field.LocalID = unpackInt(byteBuffer);
        this.ObjectData_Field.GrabOffset = unpackLLVector3(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            SurfaceInfo surfaceInfo = new SurfaceInfo();
            surfaceInfo.UVCoord = unpackLLVector3(byteBuffer);
            surfaceInfo.STCoord = unpackLLVector3(byteBuffer);
            surfaceInfo.FaceIndex = unpackInt(byteBuffer);
            surfaceInfo.Position = unpackLLVector3(byteBuffer);
            surfaceInfo.Normal = unpackLLVector3(byteBuffer);
            surfaceInfo.Binormal = unpackLLVector3(byteBuffer);
            this.SurfaceInfo_Fields.add(surfaceInfo);
        }
    }
}
