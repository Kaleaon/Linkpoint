package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ScriptSensorReply extends SLMessage {
    public Requester Requester_Field;
    public ArrayList<SensedData> SensedData_Fields = new ArrayList<>();

    public static class Requester {
        public UUID SourceID;
    }

    public static class SensedData {
        public UUID GroupID;
        public byte[] Name;
        public UUID ObjectID;
        public UUID OwnerID;
        public LLVector3 Position;
        public float Range;
        public LLQuaternion Rotation;
        public int Type;
        public LLVector3 Velocity;
    }

    public ScriptSensorReply() {
        this.zeroCoded = true;
        this.Requester_Field = new Requester();
    }

    public int CalcPayloadSize() {
        int i = 21;
        Iterator<T> it = this.SensedData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((SensedData) it.next()).Name.length + 85 + 4 + 4 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptSensorReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -8);
        packUUID(byteBuffer, this.Requester_Field.SourceID);
        byteBuffer.put((byte) this.SensedData_Fields.size());
        for (SensedData sensedData : this.SensedData_Fields) {
            packUUID(byteBuffer, sensedData.ObjectID);
            packUUID(byteBuffer, sensedData.OwnerID);
            packUUID(byteBuffer, sensedData.GroupID);
            packLLVector3(byteBuffer, sensedData.Position);
            packLLVector3(byteBuffer, sensedData.Velocity);
            packLLQuaternion(byteBuffer, sensedData.Rotation);
            packVariable(byteBuffer, sensedData.Name, 1);
            packInt(byteBuffer, sensedData.Type);
            packFloat(byteBuffer, sensedData.Range);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Requester_Field.SourceID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            SensedData sensedData = new SensedData();
            sensedData.ObjectID = unpackUUID(byteBuffer);
            sensedData.OwnerID = unpackUUID(byteBuffer);
            sensedData.GroupID = unpackUUID(byteBuffer);
            sensedData.Position = unpackLLVector3(byteBuffer);
            sensedData.Velocity = unpackLLVector3(byteBuffer);
            sensedData.Rotation = unpackLLQuaternion(byteBuffer);
            sensedData.Name = unpackVariable(byteBuffer, 1);
            sensedData.Type = unpackInt(byteBuffer);
            sensedData.Range = unpackFloat(byteBuffer);
            this.SensedData_Fields.add(sensedData);
        }
    }
}
