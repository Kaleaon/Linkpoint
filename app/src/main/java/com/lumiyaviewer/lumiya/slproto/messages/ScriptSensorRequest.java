package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ScriptSensorRequest extends SLMessage {
    public Requester Requester_Field = new Requester();

    public static class Requester {
        public float Arc;
        public float Range;
        public long RegionHandle;
        public UUID RequestID;
        public LLQuaternion SearchDir;
        public UUID SearchID;
        public byte[] SearchName;
        public LLVector3 SearchPos;
        public int SearchRegions;
        public UUID SourceID;
        public int Type;
    }

    public ScriptSensorRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.Requester_Field.SearchName.length + 73 + 4 + 4 + 4 + 8 + 1 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptSensorRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -9);
        packUUID(byteBuffer, this.Requester_Field.SourceID);
        packUUID(byteBuffer, this.Requester_Field.RequestID);
        packUUID(byteBuffer, this.Requester_Field.SearchID);
        packLLVector3(byteBuffer, this.Requester_Field.SearchPos);
        packLLQuaternion(byteBuffer, this.Requester_Field.SearchDir);
        packVariable(byteBuffer, this.Requester_Field.SearchName, 1);
        packInt(byteBuffer, this.Requester_Field.Type);
        packFloat(byteBuffer, this.Requester_Field.Range);
        packFloat(byteBuffer, this.Requester_Field.Arc);
        packLong(byteBuffer, this.Requester_Field.RegionHandle);
        packByte(byteBuffer, (byte) this.Requester_Field.SearchRegions);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Requester_Field.SourceID = unpackUUID(byteBuffer);
        this.Requester_Field.RequestID = unpackUUID(byteBuffer);
        this.Requester_Field.SearchID = unpackUUID(byteBuffer);
        this.Requester_Field.SearchPos = unpackLLVector3(byteBuffer);
        this.Requester_Field.SearchDir = unpackLLQuaternion(byteBuffer);
        this.Requester_Field.SearchName = unpackVariable(byteBuffer, 1);
        this.Requester_Field.Type = unpackInt(byteBuffer);
        this.Requester_Field.Range = unpackFloat(byteBuffer);
        this.Requester_Field.Arc = unpackFloat(byteBuffer);
        this.Requester_Field.RegionHandle = unpackLong(byteBuffer);
        this.Requester_Field.SearchRegions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
