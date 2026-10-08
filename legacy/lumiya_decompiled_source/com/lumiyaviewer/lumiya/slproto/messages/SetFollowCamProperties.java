package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class SetFollowCamProperties extends SLMessage {
    public ArrayList<CameraProperty> CameraProperty_Fields = new ArrayList<>();
    public ObjectData ObjectData_Field;

    /* loaded from: classes.dex */
    public static class CameraProperty {
        public int Type;
        public float Value;
    }

    /* loaded from: classes.dex */
    public static class ObjectData {
        public UUID ObjectID;
    }

    public SetFollowCamProperties() {
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.CameraProperty_Fields.size() * 8) + 21;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetFollowCamProperties(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -97);
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        byteBuffer.put((byte) this.CameraProperty_Fields.size());
        for (CameraProperty cameraProperty : this.CameraProperty_Fields) {
            packInt(byteBuffer, cameraProperty.Type);
            packFloat(byteBuffer, cameraProperty.Value);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            CameraProperty cameraProperty = new CameraProperty();
            cameraProperty.Type = unpackInt(byteBuffer);
            cameraProperty.Value = unpackFloat(byteBuffer);
            this.CameraProperty_Fields.add(cameraProperty);
        }
    }
}
