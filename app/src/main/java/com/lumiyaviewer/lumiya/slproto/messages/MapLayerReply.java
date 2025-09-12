package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class MapLayerReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<LayerData> LayerData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public int Flags;
    }

    public static class LayerData {
        public int Bottom;
        public UUID ImageID;
        public int Left;
        public int Right;
        public int Top;
    }

    public MapLayerReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.LayerData_Fields.size() * 32) + 25;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapLayerReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -106);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packInt(byteBuffer, this.AgentData_Field.Flags);
        byteBuffer.put((byte) this.LayerData_Fields.size());
        for (LayerData layerData : this.LayerData_Fields) {
            packInt(byteBuffer, layerData.Left);
            packInt(byteBuffer, layerData.Right);
            packInt(byteBuffer, layerData.Top);
            packInt(byteBuffer, layerData.Bottom);
            packUUID(byteBuffer, layerData.ImageID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            LayerData layerData = new LayerData();
            layerData.Left = unpackInt(byteBuffer);
            layerData.Right = unpackInt(byteBuffer);
            layerData.Top = unpackInt(byteBuffer);
            layerData.Bottom = unpackInt(byteBuffer);
            layerData.ImageID = unpackUUID(byteBuffer);
            this.LayerData_Fields.add(layerData);
        }
    }
}
