package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ObjectDeGrab extends SLMessage {
    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    public ArrayList<SurfaceInfo> SurfaceInfo_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public int LocalID;
    }

    public static class SurfaceInfo {
        public LLVector3 Binormal;
        public int FaceIndex;
        public LLVector3 Normal;
        public LLVector3 Position;
        public LLVector3 STCoord;
        public LLVector3 UVCoord;
    }

    public ObjectDeGrab() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize() {
        return (this.SurfaceInfo_Fields.size() * 64) + 41;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectDeGrab(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 119);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ObjectData_Field.LocalID);
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

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ObjectData_Field.LocalID = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
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
