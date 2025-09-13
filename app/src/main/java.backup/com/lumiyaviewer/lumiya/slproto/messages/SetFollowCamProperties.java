package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class SetFollowCamProperties extends SLMessage {
    public ArrayList<CameraProperty> CameraProperty_Fields = new ArrayList<>();
    public ObjectData ObjectData_Field;

    public static class CameraProperty {
        public int Type;
        public float Value;
    }

    public static class ObjectData {
        public UUID ObjectID;
    }

    public SetFollowCamProperties() {
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize() {
        return (this.CameraProperty_Fields.size() * 8) + 21;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetFollowCamProperties(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -97);
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        byteBuffer.put((byte) this.CameraProperty_Fields.size());
        for (CameraProperty cameraProperty : this.CameraProperty_Fields) {
            packInt(byteBuffer, cameraProperty.Type);
            packFloat(byteBuffer, cameraProperty.Value);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            CameraProperty cameraProperty = new CameraProperty();
            cameraProperty.Type = unpackInt(byteBuffer);
            cameraProperty.Value = unpackFloat(byteBuffer);
            this.CameraProperty_Fields.add(cameraProperty);
        }
    }
}
