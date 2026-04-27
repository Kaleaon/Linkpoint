package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ObjectShape extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public int ObjectLocalID;
        public int PathBegin;
        public int PathCurve;
        public int PathEnd;
        public int PathRadiusOffset;
        public int PathRevolutions;
        public int PathScaleX;
        public int PathScaleY;
        public int PathShearX;
        public int PathShearY;
        public int PathSkew;
        public int PathTaperX;
        public int PathTaperY;
        public int PathTwist;
        public int PathTwistBegin;
        public int ProfileBegin;
        public int ProfileCurve;
        public int ProfileEnd;
        public int ProfileHollow;
    }

    public ObjectShape() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 27) + 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectShape(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 98);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID);
            packByte(byteBuffer, (byte) objectData.PathCurve);
            packByte(byteBuffer, (byte) objectData.ProfileCurve);
            packShort(byteBuffer, (short) objectData.PathBegin);
            packShort(byteBuffer, (short) objectData.PathEnd);
            packByte(byteBuffer, (byte) objectData.PathScaleX);
            packByte(byteBuffer, (byte) objectData.PathScaleY);
            packByte(byteBuffer, (byte) objectData.PathShearX);
            packByte(byteBuffer, (byte) objectData.PathShearY);
            packByte(byteBuffer, (byte) objectData.PathTwist);
            packByte(byteBuffer, (byte) objectData.PathTwistBegin);
            packByte(byteBuffer, (byte) objectData.PathRadiusOffset);
            packByte(byteBuffer, (byte) objectData.PathTaperX);
            packByte(byteBuffer, (byte) objectData.PathTaperY);
            packByte(byteBuffer, (byte) objectData.PathRevolutions);
            packByte(byteBuffer, (byte) objectData.PathSkew);
            packShort(byteBuffer, (short) objectData.ProfileBegin);
            packShort(byteBuffer, (short) objectData.ProfileEnd);
            packShort(byteBuffer, (short) objectData.ProfileHollow);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectLocalID = unpackInt(byteBuffer);
            objectData.PathCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.ProfileCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathBegin = unpackShort(byteBuffer) & 65535;
            objectData.PathEnd = unpackShort(byteBuffer) & 65535;
            objectData.PathScaleX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathScaleY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathShearX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathShearY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathTwist = unpackByte(byteBuffer);
            objectData.PathTwistBegin = unpackByte(byteBuffer);
            objectData.PathRadiusOffset = unpackByte(byteBuffer);
            objectData.PathTaperX = unpackByte(byteBuffer);
            objectData.PathTaperY = unpackByte(byteBuffer);
            objectData.PathRevolutions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.PathSkew = unpackByte(byteBuffer);
            objectData.ProfileBegin = unpackShort(byteBuffer) & 65535;
            objectData.ProfileEnd = unpackShort(byteBuffer) & 65535;
            objectData.ProfileHollow = unpackShort(byteBuffer) & 65535;
            this.ObjectData_Fields.add(objectData);
        }
    }
}
